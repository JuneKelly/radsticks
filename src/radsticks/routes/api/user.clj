(ns radsticks.routes.api.user
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.db.core :as db]
            [noir.validation :as v]
            [cheshire.core :as json]
            [radsticks.routes.api.common :refer [has-valid-token?]]
            [radsticks.util :refer [ensure-json]]))


(defn user-resource-exists? [context]
  (let [params (get-in context [:request :params])]
    (db/user-exists? (params :email))))


(defn get-user-create-errors [email password name]
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
        password (params :password)
        name (params :name)]
    (get-user-create-errors email password name)))


(defn get-user-update-errors [name]
  (v/rule (v/has-value? name)
          [:name "name is required"])

  (v/get-errors))


(defn user-update-errors [params]
  (let [name (params :name)]
    (get-user-update-errors name)))


(defn can-access-user? [context]
  (let [[token-valid,
         {:keys [current-user] :as data}] (has-valid-token? context)
        requested-user-id (get-in context [:request :route-params :id])
        can-access (and token-valid
                        (= requested-user-id current-user))]
    [can-access, data]))


(defresource user-read [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get]

  :authorized?
  (fn [context]
    (can-access-user? context))

  :handle-ok
  (fn [context]
    (let [user-email (get-in context [:request :route-params :id])
          user-profile (db/get-user-profile user-email)]
      (json/generate-string user-profile))))


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
    {:errors (context :errors)})

  :post!
  (fn [context]
    (let [params (get-in context  [:request :params])
          email (params :email)
          name (params :name)
          password (params :password)
          new-profile (db/update-user! email params)]
      {:user-profile new-profile}))

  :new? ;; updates are never new resources
  false

  :respond-with-entity? true

  :multiple-representations? false

  :handle-ok
  (fn [context]
    (json/generate-string (context :user-profile))))


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
          email (params :email)
          name (params :name)
          password (params :password)
          success (db/create-user! email
                                   password
                                   name)]
      (if success
        {:user-profile (db/get-user-profile email)}
        {:error "Could not register user"})))

  :handle-created
  (fn [context]
    (json/generate-string {:userProfile (context :user-profile)})))
