(ns jetcan.routes.home
  (:use compojure.core)
  (:require [jetcan.views.layout :as layout]
            [jetcan.util :as util]))


(defn home-page []
  (layout/render
    "base.html"))
