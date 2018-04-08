(ns gfs-barcode.screens.scans
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.common.error :refer [error-alert]]
            [gfs-barcode.common.login :refer [login-view]]
            [gfs-barcode.native :refer [view text button alert]]))

(defn scans-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation
          message @(subscribe [:message])
          session-id @(subscribe [:session-id])]

      (error-alert message)
      (if (some? session-id)
        [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
         [text {:style {:font-size 30 :font-weight "100"
                        :margin-bottom 20 :text-align "right"}} "Scans Screen"]
         [button {:onPress #(navigate "Read")
                  :title "go to read"
                  :color "#841584"
                  :accessibilityLabel "go to read"}]]

        (login-view)))))

