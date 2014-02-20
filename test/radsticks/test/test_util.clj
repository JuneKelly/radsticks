(ns radsticks.test.test-util
  (:require [radsticks.db :as db]
            [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.db :as md]
            [monger.collection :as mc]
            [monger.query :as mq]
            monger.joda-time))

;; database operations
(mg/connect-via-uri! (env :db-uri))


(defn drop-database! []
  (md/drop-db (mg/get-db "radsticks_test")))


(defn populate-users! []
  (do
    (db/create-user :email "userone@example.com"
                    :pass "password1"
                    :name "User One")
    (db/create-user :email "usertwo@example.com"
                    :pass "password2"
                    :name "User Two")))


(def good-token
  (str
   "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
   ".eyJlbWFpbCI6InVzZXJvbmVAZXhhbXBsZS5"
   "jb20iLCJuYW1lIjoiVXNlciBPbmUiLCJleHA"
   "iOjE0MDA1ODUwNzMsIm5iZiI6MTM5Mjg5NTQ"
   "3M30.RONk8H71lldwvWF5Yq844-YEs-9KF1lXZo2XUGK0QLI"))


(def expired-token
  (str
   "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
   ".eyJlbWFpbCI6InVzZXJvbmVAZXhhbXBsZS5"
   "jb20iLCJuYW1lIjoiVXNlciBPbmUiLCJleHA"
   "iOjEzODQ5NDY4MzcsIm5iZiI6MTM5Mjg5NTY"
   "zN30.hcUu-CZXy_GD9lTkzetonBuWEAcdhy5ydSVxaCbnqnE"))


(def invalid-user-token
  (str
  "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
  ".eyJlbWFpbCI6Im5vdGF1c2VyQGV4YW1wbGU"
  "uY29tIiwibmFtZSI6Ik5vdCBBIFVzZXIiLCJ"
  "leHAiOjE0MDA1ODU4NDUsIm5iZiI6MTM5Mjg"
  "5NjI0NX0.qPkV_umXSEAQ45jRV6cSYCYMQwpz618jXIxxZhS0kYg"))
