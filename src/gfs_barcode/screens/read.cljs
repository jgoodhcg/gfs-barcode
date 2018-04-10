(ns gfs-barcode.screens.read
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.common.error :refer [error-alert]]
            [gfs-barcode.common.login :refer [login-view]]
            [gfs-barcode.native :refer [view text button alert bar-code-scanner]]))

(defn read-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation
          message @(subscribe [:message])
          session-id @(subscribe [:session-id])
          scanned-barcode @(subscribe [:scanned-barcode])]

      (error-alert message)

      (if (some? session-id)
        [view {:style {:flex 1 :flex-direction "column"
                       :align-items "center"
                       :justify-content "center"}}
         [text {:style {:font-size 30 :font-weight "100"
                        :margin-bottom 20 :text-align "right"}} "Scan Barcode"]

         [bar-code-scanner {:style {:height 200
                                    :width 350}
                            :onBarCodeRead
                            (fn [brjson]
                              (let [br (js->clj
                                        brjson
                                        :keywordize-keys true)
                                    data (:data br)
                                    data-conformed (->> data
                                                        (str)
                                                        (seq)
                                                        (butlast)
                                                        (clojure.string/join))
                                    type (:type br)]
                                ;; TODO fix this with something from here
                                ;; https://github.com/expo/expo/issues/345
                                (if (not= scanned-barcode data-conformed)
                                  (do
                                    (navigate "Item")
                                    (dispatch
                                     [:set-scanned-barcode data-conformed]))
                                  (println (str "scanned bar code: "
                                                data-conformed
                                                " is the same as prev scan "
                                                scanned-barcode)))))}]]

        (login-view)))))

