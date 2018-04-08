(ns gfs-barcode.screens.scans
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.native :refer [view text button]]))

(defn scans-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation]
      [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100"
                      :margin-bottom 20 :text-align "right"}} "Scans Screen"]
       [button {:onPress #(navigate "Read")
                :title "go to read"
                :color "#841584"
                :accessibilityLabel "go to read"}]])))

