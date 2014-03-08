(ns radsticks.db.core
  (:require [radsticks.util :as util]
            [noir.util.crypt :as crypt]
            [yesql.core :refer [defqueries]]
            [cheshire.core :refer [generate-string]]
            [clj-time.coerce :refer [to-sql-time]]
            [environ.core :refer [env]]))


(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname (env :db-uri)
              :user (env :db-user)
              :password (env :db-password)})

