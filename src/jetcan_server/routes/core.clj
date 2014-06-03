(ns jetcan-server.routes.core
  (:use compojure.core)
  (:require [liberator.core :refer [defresource]]
            [jetcan-server.auth :as auth]
            [jetcan-server.routes.home :refer [home-page]]
            [jetcan-server.routes.api.snippet :refer [snippet]]
            [jetcan-server.routes.api.auth :refer [authentication]]
            [jetcan-server.routes.api.user :refer [user-create
                                               user-read
                                               user-update]]))


(defroutes api-routes
  (POST "/api/auth" [] authentication)
  (POST "/api/user" [] user-create)
  (POST "/api/user/:id" [id] (user-update id))
  (GET "/api/user/:id" [id] (user-read id))
  (POST "/api/snippet" [] snippet)
  (PUT "/api/snippet/:id" [id] (snippet id))
  (DELETE "/api/snippet/:id" [id] (snippet id))
  (GET "/api/snippet/:id" [id] (snippet id))
  (GET "/api/snippet" [] snippet))


(defroutes home-routes
  (GET "/" [] (home-page)))
