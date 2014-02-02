(ns rauth.routes.home
  (:use compojure.core)
  (:require [rauth.views.layout :as layout]
            [rauth.util :as util]))


(defn home-page []
  (layout/render
    "base.html"))


(defroutes home-routes
  (GET "/" [] (home-page)))
