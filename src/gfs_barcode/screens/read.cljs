(ns gfs-barcode.screens.read
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.native :refer [view text button]]))

(defn read-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation]
      [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100"
                      :margin-bottom 20 :text-align "right"}} "Read Screen"]
       [button {:onPress #(navigate "Item")
                :title "go to item"
                :color "#841584"
                :accessibilityLabel "go to item"}]])))

