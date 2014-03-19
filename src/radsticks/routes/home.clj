(ns radsticks.routes.home
  (:use compojure.core)
  (:require [radsticks.views.layout :as layout]
            [radsticks.util :as util]))


(defn home-page []
  (layout/render
    "base.html"))
