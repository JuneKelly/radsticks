(ns radsticks.test-utils
  (:require [radsticks.db.core :refer [db-spec]]
            [radsticks.db.user :as user]
            [environ.core :refer [env]]
            [migratus.core :as migratus]
            [yesql.core :refer [defquery]]))


(defn load-queries []
  (do
    (defquery -teardown-db! "sql/schema/destroy.sql")))
(load-queries)


(defn reset-db! []
  (do (-teardown-db! db-spec)
      (migratus/migrate {:store :database
                         :migration-dir "sql/migrations"
                         :db db-spec})))


(defn populate-users! []
  (do
    (user/create! "userone@example.com"
                     "password1"
                     "User One")
    (user/create! "usertwo@example.com"
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
