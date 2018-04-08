(ns gfs-barcode.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-camera-permission
 (fn [db _]
   (:camera-permission db)))

(reg-sub
 :message
 (fn [db _]
   (:message db)))
