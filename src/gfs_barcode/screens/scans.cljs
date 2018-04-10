(ns gfs-barcode.screens.scans
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.common.error :refer [error-alert]]
            [gfs-barcode.common.login :refer [login-view]]
            [gfs-barcode.native :refer [view text touchable-highlight button alert
                                        floating-action]]))

(defn scans-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation
          message @(subscribe [:message])
          session-id @(subscribe [:session-id])
          scans @(subscribe [:scans])]

      (error-alert message)
      (if (some? session-id)
        [view {:style {:flex 1 :flex-direction "column"
                       :margin-top 40 :align-items "center"
                       :justify-content "flex-start"}}

         [text {:style {:font-size 30 :font-weight "100"
                        :margin-bottom 20 :text-align
                        "left"}} "Previous Scans"]
         (->> scans
              (map (fn [{:keys [itemCode itemDesc]}]
                     [touchable-highlight
                      {:key itemCode
                       :style {:background-color "#999"
                               :padding 10
                               :border-radius 5
                               :border-color "#555"
                               :border-width 2}
                       :on-press #(println (str "pressed " itemCode))}
                      [text
                       {:style {:color "white" :text-align "center"
                                :font-weight "bold"}} itemDesc]])))

         [floating-action {:actions []
                           :overlayColor " rgba(200,0,0,0)"
                           :color "#c80000"
                           :floatingIcon (r/as-element [text {:style {:font-size 15
                                                                      :color "white"}}
                                                        "[ |||| ]"])
                           :onPressMain #(navigate "Read")}]]

        (login-view)))))

