(ns radsticks.validation
  (:require [noir.validation :as v]))


(defn get-snippet-errors [data]
  (let [id (:id data)
        user-id (:user_id data)
        content (:content data)
        tags (:tags data)
        updated (:updated data)
        created (:created data)]
    (do
      (v/rule (v/has-value? id)
              [:id "id is required"])
      (v/rule (v/has-value? user-id)
              [:user_id "user_id is required"])
      (v/rule (v/has-value? content)
              [:content "content is required"])
      (v/rule (v/has-value? tags)
              [:tags "tags is required"])
      (v/rule (v/has-value? updated)
              [:updated "updated is required"])
      (v/rule (v/has-value? created)
              [:created "created is required"])
      (v/get-errors))))


(defn get-snippet-creation-errors [data]
  (let [user (:user data)
        content (:content data)
        tags (:tags data)]
    (do
      (v/rule (v/has-value? user)
              [:user "user is required"])
      (v/rule (v/has-value? content)
              [:content "content is required"])
      (v/rule (v/has-value? tags)
              [:tags "tags is required"])
      (v/get-errors))))
