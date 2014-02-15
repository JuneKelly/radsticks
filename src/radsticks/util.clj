(ns radsticks.util
  (:require [noir.io :as io]
            [clj-time.core :as time]
            [markdown.core :as md]))


(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))


(defn datetime
  "Shortcut for current datetime"
  []
  (time/now))


(defn uuid
  "Generate a uuid"
  []
  (str (java.util.UUID/randomUUID)))


(def rep-map
  {:representation {:media-type "application/json"}})


(defn ensure-json [m]
  "Merge the rep-map with the supplied map [m],
   ensuring that the response media-type will be json"
  (merge m rep-map))
