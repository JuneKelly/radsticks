(ns radsticks.routes.api.auth
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.auth :as auth]
            [radsticks.db :as db]
            [radsticks.util :refer [ensure-json rep-map]]))


(defresource authentication
  :available-media-types ["application/json"]
  :allowed-methods [:post]

  :malformed?
  (fn [context]
    (let [params (get-in context  [:request :params])
          username (params :username)
          password (params :password)]
      [(or (nil? username)
           (nil? password)
           (not (= (class username) java.lang.String))
           (not (= (class password) java.lang.String)))
       rep-map]))

  :handle-malformed
  (fn [context]
    {:error "post malformed"})

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
