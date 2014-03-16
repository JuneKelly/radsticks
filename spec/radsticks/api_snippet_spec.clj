(ns radsticks.api-snippet-spec
  (:require [radsticks.test-utils :as util]
            [radsticks.auth :as auth]
            [speclj.core :refer :all]
            [peridot.core :refer :all]
            [radsticks.handler :refer :all]
            [radsticks.db.snippet :as snippet]
            [cheshire.core :refer [generate-string
                                   parse-string]]))

(describe
  "reading a snippet"

  (before
   (do (util/reset-db!)
       (util/populate-users!)))

  (it "should allow an authenticated user to read their snippet"
      (let [user-email "userone@example.com"
            snippet-id (snippet/create! user-email
                                        "content is good"
                                        ["one" "two"])
            request (-> (session app)
                        (request (str "/api/snippet/" snippet-id)
                                 :request-method :get
                                 :headers {:auth_token
                                           util/good-token}))
            response (:response request)]
        (should= 200 (:status response))
        (let [snippet-data (parse-string (:body response) true)]
          (should-contain :id snippet-data)
          (should-contain :content snippet-data)
          (should-contain :user_id snippet-data)
          (should-contain :created snippet-data)
          (should-contain :updated snippet-data)
          (should-contain :tags snippet-data)
          (should (vector? (:tags snippet-data)))
          (should== ["one" "two"] (:tags snippet-data))
          (should= "userone@example.com" (:user_id snippet-data))
          (should= "content is good" (:content snippet-data))
          (should (string? (:id snippet-data))))))

    (it "should not allow an unauthenticated user to read a snippet"
      (let [user-email "userone@example.com"
            snippet-id (snippet/create! user-email
                                        "content is good"
                                        ["one" "two"])
            request (-> (session app)
                        (request (str "/api/snippet/" snippet-id)
                                 :request-method :get))
            response (:response request)]
        (should= 401 (:status response))
        (should= "Not authorized." (:body response))))

   (it "should not allow another user to access a users snippet"
      (let [user-email "userone@example.com"
            token (auth/authenticate-user "usertwo@example.com" "password2")
            snippet-id (snippet/create! user-email
                                        "content is good"
                                        ["one" "two"])
            request (-> (session app)
                        (request (str "/api/snippet/" snippet-id)
                                 :request-method :get
                                 :headers {:auth_token token}))
            response (:response request)]
        (should= 401 (:status response))
        (should= "Not authorized." (:body response)))))
