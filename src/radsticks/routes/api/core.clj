(ns radsticks.routes.api.core
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.auth :as auth]
            [radsticks.db :as db]
            [radsticks.routes.api.auth :refer [authentication]]
            [radsticks.routes.api.user :refer [user-create
                                               user-read
                                               user-update]]))

(defroutes api-routes
  (POST "/api/auth" [] authentication)
  (POST "/api/user" [] user-create)
  (PUT "/api/user/:id" [id] (user-update id))
  (GET "/api/user/:id" [id] (user-read id)))
