(ns radsticks.db
  (:require [environ.core :refer [env]]
            [noir.util.crypt :as crypt]
            [radsticks.util :as util]
            [monger.core :as mg]
            monger.joda-time
            [monger.collection :as mc]
            [monger.query :as mq]))

(def db-uri (env :db-uri))

(mg/connect-via-uri! db-uri)

;; User
;; Schema = {
;;   _id: 'email@address',
;;   pass: 'hashed password',
;;   name: 'users display name',
;;   created: Date(),
;;   last_login: Date(),
;;
;; }

(defn create-user [& {:keys [email pass name]}]
  (let [hash (crypt/encrypt pass)
        doc {:_id email
             :pass hash
             :name name
             :created (util/datetime)}]
    (try
      (do
        (mc/insert "user" doc)
        true)
      (catch Exception e false))))


(defn user-exists? [user-email]
  (not (nil? (mc/find-one-as-map "user"
                                 {:_id user-email}
                                 {:_id 1}))))


(defn get-user-profile [user-email]
  (mc/find-one-as-map "user"
                      {:_id user-email}
                      {:pass 0}))


(defn get-user-credentials! [user-email]
  (mc/find-one-as-map "user"
                      {:_id user-email},
                      {:_id 1, :pass 1}))
