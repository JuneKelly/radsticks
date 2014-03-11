(ns radsticks.routes.api.common
  (:require [radsticks.auth :as auth]))


(defn get-current-user [context]
  (let [auth-token (get-in context [:request :headers "auth_token"])
        current-user (auth/validate-user auth-token)]
    current-user))

