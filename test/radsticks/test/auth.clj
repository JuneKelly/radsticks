(ns radsticks.test.auth
  (:use clojure.test)
  (:require [radsticks.db :as db]
            [radsticks.auth :as auth]
            [radsticks.test.test-util :as util]
            [clj-time.core :as ti]
            [clj-time.coerce :as tc]))


(defn reset-db []
  (do
    (util/drop-database!)
    (util/populate-users!)))


(deftest claims
  (testing "generating claims for users"

    (do
      (reset-db)

      ;; known user, successful
      (let [user-email "userone@example.com"
            password "password1"
            current-time (ti/now)
            claim (auth/user-claim user-email)]
        (is (map? claim))

        (is (contains? claim :email))
        (is (string? (claim :email)))

        (is (contains? claim :name))
        (is (string? (claim :name)))

        (is (contains? claim :exp))
        (is (= (class (claim :exp)) org.joda.time.DateTime))

        (is (contains? claim :nbf))
        (is (= (class (claim :nbf)) org.joda.time.DateTime))

        (is (ti/after? (claim :exp) current-time))


        ))))
