(ns gfs-barcode.screens.item
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.common.error :refer [error-alert]]
            [gfs-barcode.common.login :refer [login-view]]
            [gfs-barcode.native :refer [view text button alert
                                        flat-list
                                        ;; collapsible
                                        ]]))

(defn item-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation
          message @(subscribe [:message])
          session-id @(subscribe [:session-id])
          {:keys [item-data collapsed-map]} @(subscribe [:item])
          headings (keys collapsed-map)]

      (error-alert message)

      (if (some? session-id)
        [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
         (when (some? headings)
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
                              [text {:style {:font-size 30
                                             :font-weight "100"
                                             :margin-bottom 20
                                             :text-align "left"}}
                               heading])))}])]

        (login-view)))))

