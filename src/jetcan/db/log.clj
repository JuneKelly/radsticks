(ns jetcan.db.log
  (:require [jetcan.util :as util]
            [jetcan.db.core :refer [db-spec]]
            [yesql.core :refer [defqueries]]
            [cheshire.core :refer [generate-string]]
            [clj-time.coerce :refer [to-sql-time]]))


(defn load-queries! []
  (defqueries "sql/queries/log.sql"))
(load-queries!)


(defn log! [data]
  (let [data-string (generate-string data)
        created (-> (util/datetime)
                    (to-sql-time))]
    (do
      (-create-log-entry! db-spec data-string created)
      nil)))


(defn trace [data]
  (log! (merge data {:level "trace"})))


(defn debug [data]
  (log! (merge data {:level "debug"})))


(defn info [data]
  (log! (merge data {:level "info"})))


(defn warn [data]
  (log! (merge data {:level "warn"})))


(defn error [data]
  (log! (merge data {:level "error"})))


(defn fatal [data]
  (log! (merge data {:level "fatal"})))
