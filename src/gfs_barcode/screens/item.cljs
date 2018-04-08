(ns gfs-barcode.screens.item
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.native :refer [view text button alert]]))

(defn item-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation
          message @(subscribe [:message])]

      (when (not= message "")
        (alert "Error" message
               [{:text "OK"
                 :onPress #(dispatch [:set-message ""])}]))

      [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100"
                      :margin-bottom 20 :text-align "right"}} "Item Screen"]])))

