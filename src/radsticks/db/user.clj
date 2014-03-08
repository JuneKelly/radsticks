(ns radsticks.db.user
  (:require [radsticks.util :as util]
            [noir.util.crypt :as crypt]
            [radsticks.db.core :refer [db-spec]]
            [yesql.core :refer [defqueries]]
            [clj-time.coerce :refer [to-sql-time]]))


(defn load-queries! []
  (defqueries "sql/queries/user.sql"))
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
    (first result)))


(defn get-user-credentials! [email]
  (let [result (-get-user-credentials db-spec email)]
    (first result)))


(defn update-user! [email new-values]
  (let [name (new-values :name)]
    (do (-update-user! db-spec
                       name
                       email)
        (get-user-profile email))))
