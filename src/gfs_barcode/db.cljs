(ns gfs-barcode.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::camera-permisssion boolean?)
(s/def ::message (s/or :error-message string?
                       :is-nil nil?))
(s/def ::id string?)
(s/def ::session-id (s/or :has-id ::id
                          :is-nil nil?))
(s/def ::app-db
  (s/keys :req-un [::camera-permission
                   ::message
                   ::session-id ]))

;; initial state of app-db
(def app-db {:camera-permission false
             :message nil
             :session-id nil})
