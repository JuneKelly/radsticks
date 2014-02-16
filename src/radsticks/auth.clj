(ns radsticks.auth
  (require [radsticks.db :as db]
           [environ.core  :refer [env]]
           [noir.util.crypt :as crypt]
           [clj-jwt.core  :refer :all]
           [clj-jwt.key   :refer [private-key]]
           [clj-time.core :refer [now plus days]]))


(defn- secret [] (env :secret))


;; Generate a token
(defn generate-token [claim]
  (-> claim
      jwt (sign :HS256 (secret))
      to-str))


(defn user-claim [email]
  (let [user-doc (db/get-user-profile email)]
    {:email (user-doc :email)
     :name  (user-doc :name)}))


(defn user-credentials-valid? [email password]
  (let [user-creds (db/get-user-credentials! email)]
    (and (not (nil? user-creds))
         (crypt/compare password (user-creds :pass)))))


(defn authenticate-user [email password]
  (if (user-credentials-valid? email password)
    (generate-token (user-claim email))
    nil))


;; Validate a token
(defn decode-token [token-string]
  (try
    (-> token-string str->jwt)
    (catch Exception e
      (println "Bad JWT Token")
      nil)))


(defn token-valid? [decoded-token]
  (-> decoded-token (verify (secret))))


(defn get-user-email [decoded-token]
  (get-in decoded-token [:claims :email]))


(defn validate-user [token-string]
  (let [token (decode-token token-string)]
    (if (and token (token-valid? token))
      (get-user-email token)
      nil)))

