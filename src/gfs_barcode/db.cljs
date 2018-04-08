(ns gfs-barcode.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::camera-permisssion boolean?)
(s/def ::message string?)
(s/def ::app-db
  (s/keys :req-un [::camera-permission
                   ::message]))

;; initial state of app-db
(def app-db {:camera-permission false
             :message "testing error message"})
