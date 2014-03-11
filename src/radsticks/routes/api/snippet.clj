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


(defn can-access-snippet? [context]
  (let [current-user (get-current-user context)
        snippet-id (get-in context [:request :route-params :id])
        owner (snippet/get-snippet-owner snippet-id)]
    (and (not (nil? current-user))
         (= current-user owner))))


(defresource snippet-read [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get]

  :authorized?
  (fn [context]
    (can-access-snippet? context)))
