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

(reg-sub
 :session-id
 (fn [db _]
   (:session-id db)))

(reg-sub
 :scans
 (fn [db _]
   (:scans db)))

(reg-sub
 :item
 (fn [db _]
   (:item db)))

(reg-sub
 :scanned-barcode
 (fn [db _]
   (:scanned-barcode db)))
