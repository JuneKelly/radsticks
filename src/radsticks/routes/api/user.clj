(ns radsticks.routes.api.user
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.db.user :as user]
            [radsticks.db.log :as log]
            [noir.validation :as v]
            [cheshire.core :as json]
            [radsticks.routes.api.core :refer [get-current-user]]
            [radsticks.util :refer [ensure-json]]))


(defn user-resource-exists?
  "Check if there is a user resource matching the email parameter"
  [context]
  (let [params (get-in context [:request :params])]
    (user/exists? (params :email))))


(defn get-user-create-errors
  "Validate parameters for user creation.
   Fails if wither email, name or password are missing,
   or if the email is not a valid format."
  [email password name]
  (v/rule (v/has-value? email)
          [:email "email is required"])

  (v/rule (v/is-email? email)
          [:email "email should be a valid email address"])

  (v/rule (v/has-value? password)
          [:password "password is required"])

  (v/rule (v/has-value? name)
          [:name "name is required"])

  (v/get-errors))


(defn user-create-errors [params]
  (let [email (params :email)
        password (:password params)
        name (:name params)]
    (get-user-create-errors email password name)))


(defn get-user-update-errors [name]
  (v/rule (v/has-value? name)
          [:name "name is required"])

  (v/get-errors))


(defn user-update-errors [params]
  (let [name (:name params )]
    (get-user-update-errors name)))


(defn can-access-user?
  "Check context to see if the requested user resource can
   be accessed. Returns boolean"
  [context]
  (let [current-user (get-current-user context)
        requested-user-id (get-in context [:request :route-params :id])
        can-access (= requested-user-id current-user)]
    can-access))


(defresource user-read [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get]

  :authorized?
  (fn [context]
    (can-access-user? context))

  :handle-ok
  (fn [context]
    (let [user-email (get-in context [:request :route-params :id])
          user-profile (user/get-profile user-email)]
      (do
        (log/info {:event "user:access"
                   :user user-email})
        (json/generate-string user-profile)))))


(defresource user-update [id]
  :available-media-types ["application/json"]
  :allowed-methods [:post]

  :authorized?
  can-access-user?

  :allowed?
  user-resource-exists?

  :malformed?
  (fn [context]
    (let [params (get-in context [:request :params])
          method (get-in context [:request :request-method])]
      (if (= method :post)
        (let [errors (user-update-errors params)]
          (if (empty? errors)
            false
            [true (ensure-json {:errors errors})]))
        false)))

  :handle-malformed
  (fn [context]
    {:errors (:errors context)})

  :post!
  (fn [context]
    (let [params (get-in context  [:request :params])
          email (:email params)
          name (:name params)
          password (:password params)
          new-profile (user/update! email params)]
      (do
        (log/info {:event "user:update"
                   :user email})
        {:user-profile new-profile})))

  :new? ;; updates are never new resources
  false

  :respond-with-entity? true

  :multiple-representations? false

  :handle-ok
  (fn [context]
    (json/generate-string (:user-profile context))))


(defresource user-create
  :available-media-types ["application/json"]
  :allowed-methods [:post]

  :malformed?
  (fn [context]
    (let [params (get-in context [:request :params])
          method (get-in context [:request :request-method])]
      (if (= method :post)
        (let [errors (user-create-errors params)]
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
          email (:email params)
          name (:name params)
          password (:password params)
          success (user/create! email
                                password
                                name)]
      (if success
        {:user-profile (user/get-profile email)}
        {:error "Could not register user"})))

  :handle-created
  (fn [context]
    (do
      (log/info {:event "user:registration"
                 :user (get-in context [:user-profile :email])})
      (json/generate-string {:userProfile (:user-profile context)}))))
