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
