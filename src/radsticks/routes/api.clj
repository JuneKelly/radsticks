(ns radsticks.routes.api
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.auth :as auth]
            [radsticks.db :as db]))


(defn user-resource-exists? [context]
  (let [params (get-in context [:request :params])]
    (db/user-exists? (params :email))))


(defresource user-write
  :available-media-types ["application/json"]
  :allowed-methods [:post]

  :malformed?
  (fn [context]
    (let [params (get-in context [:request :params])
          method (get-in context [:request :request-method])]
      (if (= method :post)
        (let [email (params :email)
              name (params :name)
              password (params :password)]
          [(or (empty? email)
               (empty? password)
               (not (= (class email) java.lang.String))
               (not (= (class password) java.lang.String)))
           {:representation {:media-type "application/json"}}])
        false)))

  :handle-malformed
  (fn [context]
    {:error "post malformed"})

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
        {:error "Could not register user, email already exists"})))

  :handle-created
  (fn [context]
    {:user-profile (context :user-profile)
     :error (context :error)}))

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


(defroutes api-routes
  (POST "/api/auth" [] authentication)
  (POST "/api/user" [] user-write))
