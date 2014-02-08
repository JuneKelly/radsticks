(ns radsticks.routes.api
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.auth :as auth]
            [radsticks.db :as db]))


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
       {:representation {:media-type "application/json"}}]))

  :handle-malformed
  (fn [context]
    {:error "post malformed"})

  :post!
  (fn [context]
    (let [params (get-in context [:request :params])
          username (params :username)
          password (params :password)
          token (auth/authenticate-user username password)]
      {:payload
       {:username username, :token token}}))

  :handle-created
  (fn [context] (context :payload)))


(defroutes api-routes
  (POST "/api/auth" [] authentication))
