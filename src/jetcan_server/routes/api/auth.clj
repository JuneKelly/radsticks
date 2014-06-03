(ns jetcan-server.routes.api.auth
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [noir.validation :as v]
            [jetcan-server.auth :as auth]
            [jetcan-server.db.log :as log]
            [jetcan-server.validation :refer [auth-errors]]
            [jetcan-server.util :refer [ensure-json rep-map]]))


(defresource authentication
  :available-media-types ["application/json"]
  :allowed-methods [:post]

  :malformed?
  (fn [context]
    (let [params (get-in context  [:request :params])
          errors (auth-errors params)]
      (if (empty? errors)
        false
        [true (ensure-json {:errors errors})])))

  :handle-malformed
  (fn [context]
    {:errors (context :errors)})

  :allowed?
  (fn [context]
    (let [params (get-in context [:request :params])
          email (params :email)
          password (params :password)
          token (auth/authenticate-user email password)]
      (if (not (nil? token))
        [true, {:payload
                {:email email, :token token}}]
        false)))

  :post!
  (fn [context] (comment "pass"))

  :handle-created
  (fn [context]
    (do
      (log/info {:event "user:authenticated"
                 :user (get-in context [:payload :email])})
      (context :payload))))
