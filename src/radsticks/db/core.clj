(ns radsticks.db.core
  (:require [radsticks.util :as util]
            [noir.util.crypt :as crypt]
            [yesql.core :refer [defqueries]]
            [cheshire.core :refer [generate-string]]
            [clj-time.coerce :refer [to-sql-time]]
            [environ.core :refer [env]]))


(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname (env :db-conn)
              :user (env :db-user)
              :password (env :db-password)})


;; query definitions
(defn load-queries! []
  (defqueries "sql/queries/user.sql")
  (defqueries "sql/queries/log.sql"))
(load-queries!)


(defn create-user! [email pass name]
  (let [hash (crypt/encrypt pass)
        created (util/datetime)
        doc {:_id email
             :pass hash
             :name name
             :created (util/datetime)}]
    (try
      (do
        (-create-user! db-spec
                       email
                       hash
                       name
                       (to-sql-time created))
        true)
      (catch Exception e
        (do
          (println e)
          false)))))


(defn user-exists? [email]
  (let [result (first (-user-exists? db-spec email))]
    (result :exists)))


(defn get-user-profile [email]
  (let [result (-get-user-profile db-spec email)]
    (util/sanitize-user (first result))))


(defn get-user-credentials! [email]
  (let [result (-get-user-credentials db-spec email)]
    (util/sanitize-user (first result))))


(defn log! [data]
  (let [data-string (generate-string data)]
    (do
      (-create-log-entry db-spec data-string)
      nil)))
