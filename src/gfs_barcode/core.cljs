(ns gfs-barcode.core
    (:require [reagent.core :as r :refer [atom]]
              [re-frame.core :refer [subscribe dispatch dispatch-sync]]
              [gfs-barcode.handlers]
              [cljs-react-navigation.reagent
               :refer [stack-navigator
                       stack-screen] :as nav]
              [gfs-barcode.subs]))

(def ReactNative (js/require "react-native"))
(def Expo (js/require "expo"))
(def ReactNativeFloatingAction (js/require "react-native-floating-action"))
(def floating-action (r/adapt-react-class
                     (.-FloatingAction ReactNativeFloatingAction)))
(defonce ReactNavigation (js/require "react-navigation"))
(def ReactNativeCommunications (js/require "react-native-communications"))
(def email (.-email ReactNativeCommunications))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def bar-code-scanner (r/adapt-react-class (.-BarCodeScanner Expo)))
(def svg (r/adapt-react-class (.-Svg Expo)))
(def circle (r/adapt-react-class (.-Circle (.-Svg Expo))))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def Alert (.-Alert ReactNative))

(def actions (clj->js [{:text "test"
                        :icon (js/require "./assets/images/cljs.png")
                        :name "test-fab"
                        :position 1}]))
(defn alert [title]
  (.alert Alert title))

(defn fetch-example []
  (let [data (-> (js/fetch "https://jsonplaceholder.typicode.com/posts"
                           ;; (clj->js {:method "POST"})
                           )
                 (.then #(.json %)) ;; warning: `.json` returns a promise that resolves to the parsed json body
                 (.then js->clj)
                 (.then #(println (keys (first %))))
                 (.catch #(js/console.error %)))]))


(defn home-screen [props]
  (let [navigate (get-in props [:navigation :navigate])]
    [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
     [svg {:height 100 :width 100}
      [circle {:cx 50 :cy 25 :r 25 :fill "#ff2211"
               :onPress #(navigate "Scan")}]
      [circle {:cx 50 :cy 75 :r 25 :fill "#2222ff"
               :onPress fetch-example}]]
     [text {:style {:font-size 30 :font-weight "100"
                    :margin-bottom 20 :text-align "right"}} "Home Screen"]
     [floating-action {:actions []
                       :overlayColor "rgba(0,0,0,0)"
                       :floatingIcon (r/as-element [text {:style {:font-size 15
                                                                  :color "white"}}
                                                    "[ |||| ]"])
                       :onPressMain #(email)}]]))

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
  (stack-navigator {:Home {:screen (stack-screen home-screen)}
                    :Scan {:screen (stack-screen scan-screen)}}
                   {:headerMode "none"}))

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
