(ns radsticks.validation
  (:require [validateur.validation :refer :all]))


(defn get-snippet-errors [data]
  (let [validate (validation-set
                  (presence-of :id)
                  (presence-of :user_id)
                  (presence-of :content)
                  (presence-of :tags)
                  (presence-of :updated)
                  (presence-of :created))]
    (validate data)))


(defn get-snippet-creation-errors [data]
  (let [validate (validation-set
                  (presence-of :user)
                  (presence-of :content)
                  (presence-of :tags))]
    (validate data)))
