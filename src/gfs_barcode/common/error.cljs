(ns gfs-barcode.common.error
  (:require [re-frame.core :refer [dispatch]]
            [gfs-barcode.native :refer [alert]]))


(defn error-alert [message]
  (when (some? message)
    (alert "Error" message
           [{:text "OK"
             :onPress #(dispatch [:set-message nil])}])))
