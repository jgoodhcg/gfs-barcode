(ns gfs-barcode.screens.item
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.common.error :refer [error-alert]]
            [gfs-barcode.common.login :refer [login-view]]
            [gfs-barcode.native :refer [view text button alert
                                        flat-list
                                        email
                                        floating-action]]))

(defn item-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation
          message @(subscribe [:message])
          session-id @(subscribe [:session-id])
          {:keys [item-data collapsed-map]} @(subscribe [:item])
          headings (sort (keys collapsed-map))]

      (error-alert message)

      (if (some? session-id)
        [view {:style {:flex 1 :flex-direction "column"
                       :margin-top 40 :align-items "center"
                       :padding 4}}

         (when (some? headings)
           [view
            [text {:style {:font-size 30
                           :font-weight "100"
                           :margin-bottom 10
                           :text-align "left"}}
             (str (get item-data "itemDesc") " - " (get item-data "itemCode"))]

            [flat-list
             {:data (clj->js headings)
              :keyExtractor (fn [js-item-index]
                              (let [item-index (js->clj
                                                js-item-index
                                                :keywordize-keys true)]
                                (:item item-index)))
              :renderItem (fn [js-item-with-metadata]
                            (let [item-with-metadata (js->clj
                                                      js-item-with-metadata
                                                      :keywordize-keys true)
                                  heading (:item item-with-metadata)
                                  index (:index item-with-metadata)]
                              (r/as-element
                               [view
                                [text {:style {:font-size 20
                                               :margin-top 10
                                               :font-weight "100"
                                               :text-align "left"}} heading]
                                (let [sub-data (get collapsed-map heading)
                                      sub-data-with-vals
                                      (map (fn [k] {:sub-heading k
                                                    :data (get item-data k)})
                                           sub-data)]
                                  (->> sub-data-with-vals
                                       (map (fn [d] [text {:key
                                                           (str (:sub-heading d) " - "
                                                                (:data d))}
                                                     (str (:sub-heading d) " - "
                                                          (:data d))]))))])))}]])

         [floating-action {:actions []
                           :overlayColor "rgba(0,0,0,0)"
                           :floatingIcon (r/as-element [text {:style {:font-size 15
                                                                      :color "white"}}
                                                        "@"])
                           :onPressMain #(email
                                          nil nil nil
                                          (str "GFS ITEM - " (get item-data "itemCode"))
                                          nil)}]]

        (login-view)))))

