(ns gfs-barcode.handlers
  (:require
    [re-frame.core :refer [reg-event-db reg-event-fx reg-fx ->interceptor dispatch]]
    [clojure.spec.alpha :as s]
    [gfs-barcode.db :as db :refer [app-db]]))

(defn both-start-with
  "Given two strings return the arbitrarily long string that both start with.
  Works by walking one string a character at a time and comparing to the other string.
  Case sensitive!"
  [string-1 string-2]
  (-> (take-while
       #(clojure.string/starts-with?
         string-2
         (clojure.string/join (take % string-1)))
       (range (+ 1 (count string-1))))

      (last)
      (take string-1)
      (clojure.string/join)))

(defn generate-collapsed-data-map [sorted-item-keys]
  (reduce
   (fn [collapsed-data item-key]
     (cond
       ;; first pass compares first two keys
       (string? collapsed-data)
       (let [other-item-key collapsed-data
             common-start (both-start-with item-key other-item-key)]
         (if (<= 3 (count common-start))
           {common-start [other-item-key item-key]
            :unmatched []}
           {:unmatched [other-item-key item-key]}))

       ;; comparing next key to data accumulated
       (map? collapsed-data)
       (let [commons (keys collapsed-data)
             unmatched (:unmatched collapsed-data)
             match   (some #(if (clojure.string/starts-with? item-key %) %) commons)]
         (if (some? match)
           (assoc collapsed-data match (cons item-key (get collapsed-data match)))
           ;; check the unmatched stuff
           (let [match (some
                        #(let [common-start (both-start-with item-key %)]
                           (if (<= 3 (count common-start))
                             %))
                        unmatched)]
             (if (some? match)
               (let [common-start (both-start-with match item-key)]
                 (-> collapsed-data
                     (assoc common-start [item-key match])
                     (assoc :unmatched (remove #(= match %) unmatched))))
               (assoc collapsed-data
                      :unmatched (cons item-key unmatched))))))
       :else
       {}))
   sorted-item-keys))

;; -- Interceptors ----------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/develop/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (->interceptor
        :id :validate-spec
        :after (fn [context]
                 (let [db (-> context :effects :db)]
                   (check-and-throw ::db/app-db db)
                   context)))
    ->interceptor))

;; -- Handlers --------------------------------------------------------------

(reg-fx
 :conversion-data-request
 (fn [creds]
   ;; this is were we would
   ;; use fetch to login with creds
   ;; hashed pass of course
   ;; then dispatch the return
   (println "fetching conversion data")
   (let [conversion-data (-> (js/fetch
                              "https://storage.googleapis.com/gfs-martech-webtools/fullstack-dev-homework/barcode-to-itemcode.json")
                             (.then #(.json %)) ;; warning: `.json` returns a promise that resolves to the parsed json body
                             (.then js->clj)
                             (.then #(dispatch [:set-conversion-data %]))
                             (.catch #(js/console.error %)))])))

(reg-event-db
 :set-conversion-data
 [validate-spec]
 (fn [db [_ data]]
   (assoc db :conversion-data data)))

(reg-event-fx
  :initialize-db
  [validate-spec]
  (fn [_ _]
    {:db app-db
     :conversion-data-request nil}))

(reg-event-db
  :set-camera-permission
  [validate-spec]
  (fn [db [_ value]]
    (assoc db :camera-permission value)))

(reg-event-db
  :set-message
  [validate-spec]
  (fn [db [_ value]]
    (assoc db :message value)))

(reg-event-db
 :set-session-id
 [validate-spec]
 (fn [db [_ id]]
   (assoc db :session-id id)))

(reg-fx
 :login-request
 (fn [creds]
   ;; this is were we would
   ;; use fetch to login with creds
   ;; hashed pass of course
   ;; then dispatch the return
   (println "would fetch login now")
   (dispatch [:set-session-id "123"])
   (dispatch [:get-all-scans "123"])))

(reg-fx
 :scans-request
 (fn [session-id]
   (println "would fetch scans right now with session-id")
   (dispatch [:set-scans [{:itemCode "100129"
                           :itemDesc "AP Ketchup, Tomato, w/Salt, Crown Collec"}]])))

(reg-event-fx
 :get-all-scans
 [validate-spec]
 (fn [cofx _]
   {:db (:db cofx)
    :scans-request (get-in cofx [:db :session-id])}))

(reg-event-db
 :set-scans
 [validate-spec]
 (fn [db [_ scans]]
   (assoc db :scans scans)))

(reg-event-fx
 :login
 [validate-spec]
 (fn [cofx [_ creds]]
   {:db (:db cofx)
    :login-request creds}))

(reg-fx
 :item-request
 (fn [item-code]
   (println (str "Fetching item: " item-code))
   (-> (js/fetch
        (str
         "https://api.gfs.com/ordering/rest/nutritionService/getNutritionInfo?offeringId="
         item-code
         "&offeringGroupId=00001&languageTypeCode=en"))
       (.then #(.json %)) ;; warning: `.json` returns a promise that resolves to the parsed json body
       (.then js->clj)
       (.then (fn [data]
                (println (str "Received data: " (count data)))
                (dispatch [:set-item-data (first data)])))
       (.catch #(js/console.error %)))))

(reg-event-fx
 :set-scanned-barcode
 [validate-spec]
 (fn [cofx [_ barcode]]
   (println (str "Scanned barcode: " barcode))
   (let [db (:db cofx)
         item-code (get (:conversion-data db) barcode)]

     (println (str "Converted item-code: " item-code))
     (merge {:db (assoc db :scanned-barcode barcode)}

            (if (some? item-code)
              {:item-request item-code}
              {:dispatch
               [:set-message (str "Barcode " barcode " is not a GFS product!")]})))))

(reg-event-db
 :set-item-data
 [validate-spec]
 (fn [db [_ item-data]]
   (let [sorted-keys-list (->> item-data
                               (keys)
                               (sort))
         collapsed-map (generate-collapsed-data-map
                        sorted-keys-list)]
     (println "Sorted keys: " (count sorted-keys-list))
     (assoc db :item {:item-data item-data
                      :collapsed-map collapsed-map}))))
