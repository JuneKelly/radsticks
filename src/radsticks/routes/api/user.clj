(ns radsticks.routes.api.user
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.db :as db]
            [noir.validation :as v]
            [radsticks.util :refer [ensure-json]]))


(defn user-resource-exists? [context]
  (let [params (get-in context [:request :params])]
    (db/user-exists? (params :email))))


(defn get-user-details-errors [email password name]
  (v/rule (v/has-value? email)
          [:email "email is required"])

  (v/rule (v/is-email? email)
          [:email "email should be a valid email address"])

  (v/rule (v/has-value? password)
          [:password "password is required"])

  (v/rule (v/has-value? name)
          [:name "name is required"])

  (v/get-errors))


(defn post-params-errors [params]
  (let [email (params :email)
        password (params :password)
        name (params :name)]
    (get-user-details-errors email password name)))


(defn has-valid-token [context]
  (let [auth-token (get-in context [:request :headers "auth_token"])]
    (not (nil? auth-token))))


(defresource user-read [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get]

  :allowed?
  (fn [context]
    (has-valid-token context))
  :handle-ok
  (fn [context]
    (println context)
    "hi"))


(defresource user-write
  :available-media-types ["application/json"]
  :allowed-methods [:post]

  :malformed?
  (fn [context]
    (let [params (get-in context [:request :params])
          method (get-in context [:request :request-method])]
      (if (= method :post)
        (let [errors (post-params-errors params)]
          (if (empty? errors)
            false
            [true (ensure-json {:errors errors})]))
        false)))

  :handle-malformed
  (fn [context]
    {:errors (context :errors)})

  :exists?
  user-resource-exists?

  :allowed?
  (fn [context] (not (user-resource-exists? context)))

  :post!
  (fn [context]
    (let [params (get-in context  [:request :params])
          email (params :email)
          name (params :name)
          password (params :password)
          success (db/create-user :email email
                                  :pass password
                                  :name name)]
      (if success
        {:user-profile (db/get-user-profile email)}
        {:error "Could not register user"})))

  :handle-created
  (fn [context]
    {:userProfile (context :user-profile)}))
