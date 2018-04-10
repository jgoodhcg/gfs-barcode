(ns gfs-barcode.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::camera-permisssion boolean?)
(s/def ::message (s/or :error-message string?
                       :is-nil nil?))
(s/def ::id string?)
(s/def ::session-id (s/or :has-id ::id
                          :is-nil nil?))
;; TODO scans
(s/def ::app-db
  (s/keys :req-un [::camera-permission
                   ::message
                   ::session-id ]))

;; initial state of app-db
(def app-db {:camera-permission false
             :message nil
             :item {:item-data {}
                    :collapsible-map {}}
             :scanned-barcode nil
             :session-id nil
             :conversion-data {93901100122 100129, ;; this also gets requested on load
                               93901430021 143002,
                               93901411549 411542,
                               93901806963 806961,
                               93901600509 600504,
                               93901768940 176894,
                               93901757500 175750,
                               93901165992 115250,
                               93901698254 698250,
                               50000811717 111899}})
