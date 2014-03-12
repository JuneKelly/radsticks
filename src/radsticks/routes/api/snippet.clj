(ns radsticks.routes.api.snippet
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.db.user :as user]
            [radsticks.db.log :as log]
            [radsticks.db.snippet :as snippet]
            [noir.validation :as v]
            [cheshire.core :as json]
            [radsticks.routes.api.common :refer [get-current-user]]
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


(defn is-authenticated?
  "Check if there is a valid auth token in context"
  [context]
  (not (nil? (get-current-user context))))


(defn can-access-snippet? [context]
  (let [method (get-in context [:request :request-method])]
    (if (contains? #{:get :put :delete} method)
      (is-snippet-owner-authenticated? context)
      (is-authenticated? context))))


(defresource snippet [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get :post :delete :put]

  :authorized?
  can-access-snippet?


  )
