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
                                           util/user-one-token}))
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
            snippet-id (snippet/create! user-email
                                        "content is good"
                                        ["one" "two"])
            request (-> (session app)
                        (request (str "/api/snippet/" snippet-id)
                                 :request-method :get
                                 :headers {:auth_token
                                           util/user-two-token}))
            response (:response request)]
        (should= 401 (:status response))
        (should= "Not authorized." (:body response)))))


(describe "Snippet API, post"

  (before
   (do (util/reset-db!)
       (util/populate-users!)))

  (it "should create a snippet when user is authed and params correct"
      (let [data (generate-string {:user "userone@example.com"
                                   :content "content one"
                                   :tags ["tag1" "tag2"]})
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/snippet"
                                 :request-method :post
                                 :headers {:auth_token
                                           util/user-one-token}
                                 :body data))
            response (:response request)]
        (should= 201 (:status response))
        (let [snippet-data (parse-string (:body response) true)]
          (should-contain :id snippet-data)
          (should-contain :content snippet-data)
          (should-contain :user_id snippet-data)
          (should-contain :created snippet-data)
          (should-contain :updated snippet-data)
          (should-contain :tags snippet-data)
          (should (vector? (:tags snippet-data)))
          (should== ["tag1" "tag2"] (:tags snippet-data))
          (should= "userone@example.com" (:user_id snippet-data))
          (should= "content one" (:content snippet-data))
          (should (string? (:id snippet-data))))))

  (it "should not create a snippet if user is not authenticated"
      (let [data (generate-string {:user "userone@example.com"
                                   :content "content one"
                                   :tags ["tag1" "tag2"]})
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/snippet"
                                 :request-method :post
                                 :body data))
            response (:response request)]
        (should= 401 (:status response))
        (should= "Not authorized." (:body response))))

  (it "should not create a snippet if content is missing"
      (let [data (generate-string {:user "userone@example.com"
                                   :tags ["tag1" "tag2"]})
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/snippet"
                                 :request-method :post
                                 :headers {:auth_token util/user-one-token}
                                 :body data))
            response (:response request)]
        (should= 400 (:status response))
        (let [response-json (parse-string (:body response) true)]
          (should (contains? response-json :errors))
          (should (vector? (:errors response-json)))
          (should= ["content is required"] (:errors response-json)))))

  (it "should not create a snippet if tags are missing"
      (let [data (generate-string {:user "userone@example.com"
                                   :content "c"})
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/snippet"
                                 :request-method :post
                                 :headers {:auth_token util/user-one-token}
                                 :body data))
            response (:response request)]
        (should= 400 (:status response))
        (let [response-json (parse-string (:body response) true)]
          (should (contains? response-json :errors))
          (should (vector? (:errors response-json)))
          (should= ["tags is required"] (:errors response-json)))))

  (it "should not create a snippet if user is missing"
      (let [data (generate-string {:content "c"
                                   :tags ["tag1" "tag2"]})
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/snippet"
                                 :request-method :post
                                 :headers {:auth_token util/user-one-token}
                                 :body data))
            response (:response request)]
        (should= 400 (:status response))
        (let [response-json (parse-string (:body response) true)]
          (should (contains? response-json :errors))
          (should (vector? (:errors response-json)))
          (should= ["user is required"] (:errors response-json))))))


(describe "updating snippets"
  (before (do (util/reset-db!)
              (util/populate-users!)))

  (it "should allow a user to update a snippet of theirs"
      (let [snippet-id (snippet/create! "userone@example.com"
                                        "content one"
                                        ["one" "two"])
            snippet (snippet/get-by-id snippet-id)
            data (generate-string (assoc snippet :content "content two"
                                         :tags ["two" "three"]))
            request (-> (session app)
                        (content-type "application/json")
                        (request (str "/api/snippet/" snippet-id)
                                 :request-method :put
                                 :headers {:auth_token util/user-one-token}
                                 :body data))
            response (:response request)]
        (should= 200 (:status response))
        (should= "application/json;charset=UTF-8"
                 (get-in response [:headers "Content-Type"]))
        (let [response-json (parse-string (:body response) true)]
          (should-contain :user_id response-json)
          (should-contain :id response-json)
          (should-contain :content response-json)
          (should-contain :created response-json)
          (should-contain :updated response-json)
          (should-contain :tags response-json)
          (should= snippet-id (:id response-json))
          (should-not= "content one" (:content response-json))
          (should= "content two" (:content response-json))
          (should-not== ["one", "two"] (:tags response-json))
          (should== ["two", "three"] (:tags response-json)))))

  (it "should not allow a user to update a snippet they do not own"
      (let [snippet-id (snippet/create! "usertwo@example.com"
                                        "content"
                                        ["one" "two"])
            snippet (snippet/get-by-id snippet-id)
            data (generate-string (assoc snippet :content "content two"
                                         :tags ["two" "three"]))
            request (-> (session app)
                        (content-type "application/json")
                        (request (str "/api/snippet/" snippet-id)
                                 :request-method :put
                                 :headers {:auth_token util/user-one-token}
                                 :body data))
            response (:response request)]
        (should= 401 (:status response))
        (should= "Not authorized." (:body response))))

  (it "should not allow a updating a snippet without an auth token"
      (let [snippet-id (snippet/create! "userone@example.com"
                                        "content"
                                        ["one" "two"])
            snippet (snippet/get-by-id snippet-id)
            data (generate-string (assoc snippet :content "content two"
                                         :tags ["two" "three"]))
            request (-> (session app)
                        (content-type "application/json")
                        (request (str "/api/snippet/" snippet-id)
                                 :request-method :put
                                 :body data))
            response (:response request)]
        (should= 401 (:status response))
        (should= "Not authorized." (:body response))))

  (it "should not allow a updating a snippet if payload is malformed"
      (let [snippet-id (snippet/create! "userone@example.com"
                                        "content"
                                        ["one" "two"])
            snippet (snippet/get-by-id snippet-id)
            data (generate-string (dissoc snippet :content))
            request (-> (session app)
                        (content-type "application/json")
                        (request (str "/api/snippet/" snippet-id)
                                 :request-method :put
                                 :headers {:auth_token util/user-one-token}
                                 :body data))
            response (:response request)]
        (should= 400 (:status response))
        (let [response-json (parse-string (:body response) true)]
          (should (contains? response-json :errors))
          (should (vector? (:errors response-json)))
          (should= ["content is required"] (:errors response-json))))))


(run-specs)
