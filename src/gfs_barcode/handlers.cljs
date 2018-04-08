(ns gfs-barcode.handlers
  (:require
    [re-frame.core :refer [reg-event-db reg-event-fx reg-fx ->interceptor dispatch]]
    [clojure.spec.alpha :as s]
    [gfs-barcode.db :as db :refer [app-db]]))

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

(reg-event-db
  :initialize-db
  [validate-spec]
  (fn [_ _]
    app-db))

(reg-event-db
  :set-camera-permission
  [validate-spec]
  (fn [db [_ value]]
    (assoc db :camera-permission value)))

(reg-event-db
  :set-message
  validate-spec
  (fn [db [_ value]]
    (assoc db :message value)))

(reg-event-db
 :set-session-id
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
   (dispatch [:set-session-id "123"])))

(reg-event-fx
 :login
 validate-spec
 (fn [cofx [_ creds]]
   (println (keys cofx))
   {:db (:db cofx)
    :login-request creds}))
