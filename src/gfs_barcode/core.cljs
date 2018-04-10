(ns gfs-barcode.core
    (:require [reagent.core :as r :refer [atom]]
              [re-frame.core :refer [subscribe dispatch dispatch-sync]]
              [gfs-barcode.handlers]
              [cljs-react-navigation.reagent
               :refer [stack-navigator
                       stack-screen] :as nav]
              [gfs-barcode.subs]
              [gfs-barcode.screens.scans :refer [scans-screen]]
              [gfs-barcode.screens.read :refer [read-screen]]
              [gfs-barcode.screens.item :refer [item-screen]]
              [gfs-barcode.native :refer [email
                                          alert
                                          app-registry
                                          text
                                          view
                                          image
                                          bar-code-scanner
                                          svg
                                          circle
                                          touchable-highlight
                                          button]]))

(def actions (clj->js [{:text "test"
                        :icon (js/require "./assets/images/cljs.png")
                        :name "test-fab"
                        :position 1}]))

(defn scan-screen [props]
  (let [camera-permission (subscribe [:get-camera-permission])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100"
                      :margin-bottom 20 :text-align "right"}} "you did it!"]
       [bar-code-scanner {:style {:height 200
                                  :width 200}
                          :onBarCodeRead (fn [brjson]
                                           (let [br (js->clj
                                                     brjson
                                                     :keywordize-keys true)
                                                 data (:data br)
                                                 type (:type br)]
                                             (alert (str type "-" data))))}]])))

(def AllRoutesStack
  (stack-navigator {:Scans {:screen (stack-screen scans-screen)}
                    :Read  {:screen (stack-screen read-screen)}
                    :Item  {:screen (stack-screen item-screen)}}
                   {:headerMode "none"
                    :initRouteName "Scans"}))

;; (defn app-root []
;;   (r/create-class
;;    (let [nav-state (subscribe [::nav/routing-state])]
;;      {:component-will-mount (fn []
;;                               (when-not @nav-state
;;                                 (dispatch-sync [:reset-routing-state])))
;;       :reagent-render       (fn []
;;                               [router {:root-router AllRoutesStack
;;                                        :init-route-name "Home"}])})))

(defn app-root []
  (fn []
    [:> AllRoutesStack {}]))

(defn init []
  (dispatch-sync [:initialize-db])
  ;; (.registerRootComponent Expo (r/reactify-component app-root))
  (.registerComponent app-registry "main" #(r/reactify-component app-root))
  )
