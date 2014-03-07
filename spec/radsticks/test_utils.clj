(ns radsticks.test-utils
  (:require [radsticks.db.core :as db]
            [environ.core :refer [env]]
            [yesql.core :refer [defquery]]))


(defn load-queries []
  (do
    (defquery -reset-db! "sql/schema/create.sql")))
(load-queries)


(defn reset-db! []
  (do (-reset-db! db/db-spec)))


(defn populate-users! []
  (do
    (db/create-user! "userone@example.com"
                     "password1"
                     "User One")
    (db/create-user! "usertwo@example.com"
                     "password2"
                     "User Two")))


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
