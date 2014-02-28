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


(defn sanitize-user [user]
  (-> user
      (assoc :email (user :_id))
      (dissoc :_id)
      (dissoc :pass)))


(defn user-exists? [user-email]
  (not (nil? (mc/find-one-as-map "user"
                                 {:_id user-email}
                                 {:_id 1}))))


(defn update-user [user-email data]
  (if (user-exists? user-email)
    (let [new-data (select-keys data [:name])
          new-profile (mc/find-and-modify
                        "user"
                        {:_id user-email}
                        {:$set new-data}
                        :return-new true)]
      (sanitize-user new-profile))))


(defn get-user-profile [user-email]
  (let [user (mc/find-one-as-map "user"
                                 {:_id user-email}
                                 {:pass 0})]
    (if (not (nil? user))
      (sanitize-user user)
      nil)))


(defn get-user-credentials! [user-email]
  (mc/find-one-as-map "user"
                      {:_id user-email},
                      {:_id 1, :pass 1}))


;; snippet = {
;;   text: String,
;;   tags: [String],
;;   created: Datetime,
;;   user: String (-> User)
;; }
(defn create-snippet [user, text, tags]
  (if (user-exists? user)
    (let [snippet-id (util/slug)
          doc {:_id snippet-id
               :user user
               :text text
               :tags tags
               :created (util/datetime)}]
      (do
        (mc/insert "snippets" doc)
        snippet-id))))
