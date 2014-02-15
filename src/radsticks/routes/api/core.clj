(ns radsticks.routes.api.core
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [radsticks.auth :as auth]
            [radsticks.db :as db]
            [radsticks.routes.api.auth :refer [authentication]]
            [radsticks.routes.api.user :refer [user-write]]))


(defroutes api-routes
  (POST "/api/auth" [] authentication)
  (POST "/api/user" [] user-write))
