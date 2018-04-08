(ns gfs-barcode.common.login
  (:require [gfs-barcode.native :refer [view text text-input button alert]]
            [re-frame.core :refer [dispatch]]))

(defn login-view []
  [view {:style {:flex 1
                 :flex-direction "column"
                 :align-items "center"
                 :justify-content "center"}}

   [text-input {:height 40 :width 200 :placeholder "email"}]
   [text-input {:height 40 :width 200 :placeholder "password"
                :style {:margin-bottom 25}}]
   [button {:onPress #(dispatch [:login {:user "123" :pass "123"}])
            :title "Login"
            :color "#c80000"
            :accessibilityLabel "Login"}]])
