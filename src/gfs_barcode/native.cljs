(ns gfs-barcode.native
  (:require [reagent.core :as r]))

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
(def button (r/adapt-react-class (.-Button ReactNative)))



