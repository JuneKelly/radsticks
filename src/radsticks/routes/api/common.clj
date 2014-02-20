(ns radsticks.routes.api.common
  (:require [radsticks.auth :as auth]))


(defn has-valid-token? [context]
  (let [auth-token (get-in context [:request :headers "auth_token"])
        current-user (auth/validate-user auth-token)]
    [(and (not (nil? auth-token))
          (not (nil? current-user)))
     {:current-user current-user}]))
