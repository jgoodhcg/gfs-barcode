(ns gfs-barcode.screens.login
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [gfs-barcode.handlers]
            [gfs-barcode.subs]
            [gfs-barcode.common.error :refer [error-alert]]
            [gfs-barcode.native :refer [view text text-input button alert]]))

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
          message @(subscribe [:message])
          session-id @(subscribe [:session-id])]

      (error-alert message)

      [view {:style {:flex 1
                     :flex-direction "column"
                     :align-items "center"
                     :justify-content "center"}}

       [text-input {:height 40 :width 200 :placeholder "email"}]
       [text-input {:height 40 :width 200 :placeholder "password"
                    :style {:margin-bottom 25}}]

       (if (some? session-id)
         [button {:onPress #(dispatch [:set-session-id nil])
                  :title "Logout"
                  :style {:width 200}
                  :color "#841584"
                  :accessibilityLabel "Logout"}]

         [button {:onPress #(dispatch [:login {:user "123" :pass "123"}])
                  :title "Login"
                  :width 200
                  :style {:width 200}
                  :color "#841584"
                  :accessibilityLabel "Login"}])

       (when (some? session-id)
         [button {:onPress #(navigate "Scans")
                  :title "Recent Scans"
                  :color "#841584"
                    :accessibilityLabel "Recent Scans"}])])))
