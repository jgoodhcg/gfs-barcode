(ns gfs-barcode.core
    (:require [reagent.core :as r :refer [atom]]
              [re-frame.core :refer [subscribe dispatch dispatch-sync]]
              [gfs-barcode.handlers]
              [cljs-react-navigation.reagent
               :refer [stack-navigator
                       stack-screen] :as nav]
              [gfs-barcode.subs]
              [gfs-barcode.native :refer [email
                                          app-registry
                                          text
                                          view
                                          image
                                          bar-code-scanner
                                          svg
                                          circle
                                          touchable-highlight
                                          Alert
                                          button]]))

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

(defn login-screen
  "Each Screen will receive two props:
 - screenProps - Extra props passed down from the router (rarely used)
 - navigation  - The main navigation functions in a map as follows:
   {:state     - routing state for this screen
    :dispatch  - generic dispatch fn
    :goBack    - pop's the current screen off the stack
    :navigate  - most common way to navigate to the next screen
    :setParams - used to change the params for the current screen}"
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation]
      [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100"
                      :margin-bottom 20 :text-align "right"}} "Login Screen"]
       [button {:onPress #(navigate "Scans")
                :title "go to scans"
                :color "#841584"
                :accessibilityLabel "go to scans"}]])))

(defn scans-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation]
      [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100"
                      :margin-bottom 20 :text-align "right"}} "Scan Screen"]
       [button {:onPress #(navigate "Read")
                :title "go to read"
                :color "#841584"
                :accessibilityLabel "go to read"}]])))

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

(defn item-screen
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation]
      [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100"
                      :margin-bottom 20 :text-align "right"}} "Item Screen"]])))

(def AllRoutesStack
  (stack-navigator {:Login {:screen (stack-screen login-screen)}
                    :Scans {:screen (stack-screen scans-screen)}
                    :Read  {:screen (stack-screen read-screen)}
                    :Item  {:screen (stack-screen item-screen)}}
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
