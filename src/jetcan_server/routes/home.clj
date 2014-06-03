(ns jetcan-server.routes.home
  (:use compojure.core)
  (:require [jetcan-server.views.layout :as layout]
            [jetcan-server.util :as util]))


(defn home-page []
  (layout/render
    "base.html"))
