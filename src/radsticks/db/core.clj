(ns radsticks.db.core
  (:require [radsticks.util :as util]
            [yesql.core :refer [defqueries]]
            [environ.core :refer [env]]))


(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname (env :db-uri)
              :user (env :db-user)
              :password (env :db-password)})


;; query definitions
(defqueries "src/radsticks/db/sql/queries/user.sql")
