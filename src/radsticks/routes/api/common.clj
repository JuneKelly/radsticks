(ns radsticks.routes.api.common
  (:require [radsticks.auth :as auth]))


(defn has-valid-token? [context]
  (let [auth-token (get-in context [:request :headers "auth_token"])
        current-user (auth/validate-user auth-token)]
    [(and (not (nil? auth-token))
          (not (nil? current-user)))
     {:current-user current-user}]))


(defn get-current-user [context]
  (let [[token-valid, {:keys [current-user]}] has-valid-token?]
    (if token-valid
      current-user
      nil)))
