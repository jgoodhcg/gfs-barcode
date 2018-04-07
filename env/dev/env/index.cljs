(ns env.index
  (:require [env.dev :as dev]))

;; undo main.js goog preamble hack
(set! js/window.goog js/undefined)

(-> (js/require "figwheel-bridge")
    (.withModules #js {"./assets/icons/loading.png" (js/require "../../../assets/icons/loading.png"), "./assets/icons/Scan_barcode.png" (js/require "../../../assets/icons/Scan_barcode.png"), "react-native-floating-action" (js/require "react-native-floating-action"), "expo" (js/require "expo"), "./assets/images/cljs.png" (js/require "../../../assets/images/cljs.png"), "./assets/icons/app.png" (js/require "../../../assets/icons/app.png"), "react-native" (js/require "react-native"), "react-navigation" (js/require "react-navigation"), "react" (js/require "react"), "create-react-class" (js/require "create-react-class")}
)
    (.start "main" "expo" "192.168.1.84"))
