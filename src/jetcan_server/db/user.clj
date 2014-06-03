(ns jetcan-server.db.user
  (:require [jetcan-server.util :as util]
            [noir.util.crypt :as crypt]
            [jetcan-server.db.core :refer [db-spec]]
            [yesql.core :refer [defqueries]]
            [clj-time.coerce :refer [to-sql-time]]))


(defn load-queries! []
  (defqueries "sql/queries/user.sql"))
(load-queries!)


(defn create! [email pass name]
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


(defn exists? [email]
  (let [result (first (-user-exists? db-spec email))]
    (result :exists)))


(defn get-profile [email]
  (let [result (-get-user-profile db-spec email)]
    (first result)))


(defn get-credentials! [email]
  (let [result (-get-user-credentials db-spec email)]
    (first result)))


(defn update! [email new-values]
  (let [name (new-values :name)]
    (do (-update-user! db-spec
                       name
                       email)
        (get-profile email))))
