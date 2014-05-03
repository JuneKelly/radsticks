(ns radsticks.validation
  (:require [validateur.validation :refer :all]))


(defn snippet-errors [data]
  (let [validate (validation-set
                  (presence-of :id)
                  (presence-of :user_id)
                  (presence-of :content)
                  (presence-of :tags)
                  (presence-of :updated)
                  (presence-of :created))]
    (validate data)))


(defn snippet-creation-errors [data]
  (let [validate (validation-set
                  (presence-of :user)
                  (presence-of :content)
                  (presence-of :tags))]
    (validate data)))


(def email-regex
  #"(?i)[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]")


(defn user-creation-errors [data]
  (let [validate (validation-set
                  (presence-of :email)
                  (format-of :email
                             :format email-regex
                             :message "must be an email address")
                  (presence-of :password)
                  (presence-of :name))]
    (validate data)))


(defn user-update-errors [data]
  (let [validate (validation-set
                  (presence-of :name)
                  (validate-with-predicate :name
                                           #(string? (:name %)))
                  (presence-of :email))]
    (validate data)))


(defn auth-errors [data]
  (let [validate (validation-set
                  (presence-of :email)
                  (validate-with-predicate :email
                                           #(string? (:email %)))
                  (presence-of :password)
                  (validate-with-predicate :password
                                           #(string? (:password %))))]
    (validate data)))
