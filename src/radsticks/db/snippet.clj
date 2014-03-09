(ns radsticks.db.snippet
  (:require [radsticks.util :as util]
            [radsticks.db.core :refer [db-spec]]
            [yesql.core :refer [defqueries]]
            [clj-time.coerce :refer [to-sql-time]]))

(comment "")

(defn load-queries! []
  (defqueries "sql/queries/snippet.sql"))
(load-queries!)


(defn create! [user-email, content, tags]
  (let [created (to-sql-time (util/datetime))
        updated (to-sql-time (util/datetime))]
    (do
      (-create-snippet<! db-spec
                        user-email
                        content
                        tags
                        created
                        updated))))


(defn exists? [snippet-id]
  (let [result (first (-snippet-exists? db-spec snippet-id))]
    (result :exists)))


(defn get-snippet [snippet-id]
  (let [result (-get-snippet db-spec snippet-id)]
    (first result)))
