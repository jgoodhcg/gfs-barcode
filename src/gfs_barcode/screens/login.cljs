(ns gfs-barcode.screens.login
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.native :refer [view text button alert]]))

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
    (let [{:keys [navigate goBack]} navigation
          message @(subscribe [:message])]

      (when (not= message "")
        (alert "Error" message
               [{:text "OK"
                 :onPress #(dispatch [:set-message ""])}]))

      [view {:style {:flex 1 :flex-direction "column" :margin-top 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100"
                      :margin-bottom 20 :text-align "right"}} "Login Screen"]
       [button {:onPress #(dispatch [:set-message "yeayea"])
                :title "set error message"
                :color "#841584"
                :accessibilityLabel "go to scans"}]
       [button {:onPress #(navigate "Scans")
                :title "go to scans"
                :color "#841584"
                :accessibilityLabel "go to scans"}]])))
