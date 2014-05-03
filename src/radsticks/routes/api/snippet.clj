(ns radsticks.routes.api.snippet
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.db.user :as user]
            [radsticks.db.log :as log]
            [radsticks.db.snippet :as snippet]
            [clj-time.core :as time]
            [clj-time.coerce :refer [from-sql-time to-string]]
            [cheshire.core :as json]
            [radsticks.routes.api.core :refer [get-current-user
                                               is-authenticated?]]
            [radsticks.validation :refer [snippet-errors
                                          snippet-creation-errors]]
            [radsticks.util :refer [ensure-json
                                    json-coerce]]))


(defn snippet-owner-authenticated?
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
  (let [method (get-in context [:request :request-method])
        snippet-id (get-in context [:request :route-params :id])]
    (cond

     (and (= :get method) (nil? snippet-id))
     (is-authenticated? context)

     (contains? #{:get :put :delete} method)
     (snippet-owner-authenticated? context)

     :else
     (is-authenticated? context))))


(defn snippet-as-json [snippet-id]
  (let [snippet-data (snippet/get-by-id snippet-id)]
    (json/generate-string snippet-data)))


(defn post-malformed? [context]
  (let [data (get-in context [:request :body-params])
        errors (snippet-creation-errors data)]
      (if (empty? errors)
        false
        [true, (ensure-json {:errors errors})])))


(defn put-malformed? [context]
  (let [snippet (get-in context [:request :body-params])
        errors (snippet-errors snippet)]
    (if (empty? errors)
      false
      [true, (ensure-json {:errors errors})])))


(defresource snippet [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get :post :delete :put]

  :authorized?
  can-access-snippet?

  :allowed?
  (fn [context]
    (let [snippet-id (get-in context [:request :route-params :id])
          method (get-in context [:request :request-method])]
      (cond
       (= :get method)
       true
       (contains? #{:put :delete} method)
       (snippet/exists? snippet-id)
       :else
       true)))

  :exists?
  (fn [context]
    (let [snippet-id (get-in context [:request :route-params :id])]
      (if (nil? snippet-id)
        true  ;; no snippet-id specified, requesting list
        (snippet/exists? snippet-id))))

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

  :handle-malformed
  (fn [context]
    {:errors (:errors context)})

  :conflict?
  (fn [context]
    (let [params (json-coerce (get-in context [:request :body-params]))
          snippet-id (:id params)
          existing (json-coerce (snippet/get-by-id snippet-id))]
      (or (not= (:updated params) (:updated existing))
          (not= (:created params) (:created existing))
          (not= (:user_id params) (:user_id existing))
          (not= (:id params) (:id existing)))))

  :post!
  (fn [context]
    (let [params (get-in context [:request :body-params])
          snippet-id (snippet/create! (:user params)
                                      (:content params)
                                      (:tags params))]
      (do
        (log/info {:event "snippet:create"
                   :user (:user params)
                   :snippet-id snippet-id})
        {:snippet-id snippet-id})))

  :put!
  (fn [context]
    (let [params (get-in context [:request :body-params])]
      (do
        (log/info {:event "snippet:update"
                   :user (get-current-user context)
                   :snippet-id (:id params)})
        (snippet/update! (:id params)
                         (:content params)
                         (:tags params)))))

  :respond-with-entity?
  (fn [context]
    (let [method (get-in context [:request :request-method])]
      (not (= :delete method))))

  :new?
  (fn [context]
    (let [method (get-in context [:request :request-method])]
      (cond (= method :post)
            true
            (= method :put)
            false
            :else
            false)))

  :delete!
  (fn [context]
    (let [snippet-id (get-in context [:request :route-params :id])]
      (do
        (log/info {:event "snippet:delete"
                   :user (get-current-user context)
                   :snippet-id snippet-id})
        (snippet/delete! snippet-id))))

  :handle-ok
  (fn [context]
    (let [snippet-id (get-in context [:request :route-params :id])]
      (if (nil? snippet-id)
        (-> (snippet/get-by-user-id (get-current-user context))
            (json/generate-string))
        (snippet-as-json snippet-id))))

  :handle-created
  (fn [context]
    (let [snippet-id (:snippet-id context)]
      (snippet-as-json snippet-id))))
