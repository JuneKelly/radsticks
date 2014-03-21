(ns radsticks.routes.api.snippet
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.db.user :as user]
            [radsticks.db.log :as log]
            [radsticks.db.snippet :as snippet]
            [noir.validation :as v]
            [cheshire.core :as json]
            [radsticks.routes.api.core :refer [get-current-user
                                                 is-authenticated?]]
            [radsticks.util :refer [ensure-json]]))


(defn is-snippet-owner-authenticated?
  "Checks the request context to see if the currently
   authenticated user is the owner of the snippet
   resource. returns boolean"
  [context]
  (let [current-user (get-current-user context)
        snippet-id (get-in context [:request :route-params :id])
        owner (snippet/get-snippet-owner snippet-id)]
    (and (not (nil? current-user))
         (= current-user owner))))


(defn can-access-snippet? [context]
  (let [method (get-in context [:request :request-method])]
    (if (contains? #{:get :put :delete} method)
      (is-snippet-owner-authenticated? context)
      (is-authenticated? context))))


(defn snippet-json [snippet-id]
  (let [snippet-data (snippet/get-snippet snippet-id)]
    (json/generate-string snippet-data)))


(defn post-malformed? [context]
  false)


(defn put-malformed? [context]
  (comment "todo"))


(defresource snippet [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get :post :delete :put]

  :authorized?
  can-access-snippet?

  :allowed?
  (fn [context]
    (let [snippet-id (get-in context [:request :route-params :id])
          method (get-in context [:request :request-method])]
      (if (contains? #{:get :put :delete} method)
        (snippet/exists? snippet-id)
        true)))

  :exists?
  (fn [context]
    (let [snippet-id (get-in context [:request :route-params :id])]
      (snippet/exists? snippet-id)))

  :can-put-to-missing?
  false

  :malformed?
  (fn [context]
    (let [method (get-in context [:request :request-method])]
      (cond
       (= :post method)
       (post-malformed? context)
       (= :put method)
       (put-malformed? context)
       :else
       false)))

  :conflict?
  (fn [context]
    (comment "todo, with the put action"))

  :post!
  (fn [context]
    (let [params (get-in context [:request :body-params])
          snippet-id (snippet/create! (:user params)
                                      (:content params)
                                      (:tags params))]
      {:snippet-id snippet-id}))

  :put!
  (fn [context]
    (comment "todo"))

  :delete!
  (fn [context]
    (comment "todo"))

  :handle-ok
  (fn [context]
    (let [snippet-id (get-in context [:request :route-params :id])]
      (snippet-json snippet-id)))

  :handle-created
  (fn [context]
    (let [snippet-id (:snippet-id context)]
      (snippet-json snippet-id))))
