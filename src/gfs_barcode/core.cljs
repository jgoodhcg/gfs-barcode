(ns gfs-barcode.core
    (:require [reagent.core :as r :refer [atom]]
              [re-frame.core :refer [subscribe dispatch dispatch-sync]]
              [gfs-barcode.handlers]
              [gfs-barcode.subs]))

(def ReactNative (js/require "react-native"))
(def Expo (js/require "expo"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def bar-code-scanner (r/adapt-react-class (.-BarCodeScanner Expo)))
(def svg (r/adapt-react-class (.-Svg Expo)))
(def circle (r/adapt-react-class (.-Circle (.-Svg Expo))))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def Alert (.-Alert ReactNative))

(defn alert [title]
  (.alert Alert title))

(defn app-root []
  (let [camera-permission (subscribe [:get-camera-permission])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "right"}} "you did it!"]
       [svg {:height 100 :width 100}
        [circle {:cx 50 :cy 50 :r 25 :fill "#ff22ff"}]]
       [bar-code-scanner {:style {:height 200
                                  :width 200}
                          :onBarCodeRead (fn [brjson]
                                           (let [br (js->clj
                                                     brjson
                                                     :keywordize-keys true)
                                                 data (:data br)
                                                 type (:type br)]
                                             (alert (str type "-" data))))}]
       ])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
