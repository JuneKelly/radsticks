(ns radsticks.routes.core
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.auth :as auth]
            [radsticks.routes.home :refer [home-page]]
            [radsticks.routes.api.snippet :refer [snippet]]
            [radsticks.routes.api.auth :refer [authentication]]
            [radsticks.routes.api.user :refer [user-create
                                               user-read
                                               user-update]]))


(defroutes api-routes
  (POST "/api/auth" [] authentication)
  (POST "/api/user" [] user-create)
  (POST "/api/user/:id" [id] (user-update id))
  (GET "/api/user/:id" [id] (user-read id))
  (POST "/api/snippet" [] snippet)
  (PUT "/api/snippet/:id" [id] (snippet id))
  (GET "/api/snippet/:id" [id] (snippet id)))


(defroutes home-routes
  (GET "/" [] (home-page)))
