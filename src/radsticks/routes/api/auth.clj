(ns radsticks.routes.api.auth
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [noir.validation :as v]
            [radsticks.auth :as auth]
            [radsticks.db :as db]
            [radsticks.util :refer [ensure-json rep-map]]))


(defn get-auth-errors [params]
  (let [username (params :username)
        password (params :password)]
    (v/rule (v/has-value? username)
            [:username "username is required"])
    (v/rule (v/has-value? password)
            [:password "password is required"])
    (v/rule #(= (class username) java.lang.String)
            [:username "username must be a string"])
    (v/rule #(= (class password) java.lang.String)
            [:password "password must be a string"])
    (v/get-errors)))


(defresource authentication
  :available-media-types ["application/json"]
  :allowed-methods [:post]

  :malformed?
  (fn [context]
    (let [params (get-in context  [:request :params])
          errors (get-auth-errors params)]
      (if (empty? errors)
        false
        [true (ensure-json {:errors errors})])))

  :handle-malformed
  (fn [context]
    {:errors (context :errors)})

  :allowed?
  (fn [context]
    (let [params (get-in context [:request :params])
          username (params :username)
          password (params :password)
          token (auth/authenticate-user username password)]
      (if (not (nil? token))
        [true, {:payload
                {:username username, :token token}}]
        false)))

  :post!
  (fn [context] (comment "pass"))

  :handle-created
  (fn [context] (context :payload)))
