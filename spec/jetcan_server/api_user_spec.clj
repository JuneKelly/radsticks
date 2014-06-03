(ns jetcan-server.api-user-spec
  (:require [jetcan-server.test-utils :as util]
            [speclj.core :refer :all]
            [peridot.core :refer :all]
            [jetcan-server.handler :refer :all]
            [jetcan-server.db.user :as user]
            [cheshire.core :refer [generate-string
                                   parse-string]]))


(describe
  "user creation"

  (before
   (do (util/reset-db!)
       (util/populate-users!)))

  (it "should allow a user to be created when params are correct"
    (let [request-body
          "{\"email\":\"qwer@example.com\",
            \"password\":\"password3\",
            \"name\": \"Qwer\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/user"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (= (:status response) 201))
      (should (contains? response-json :userProfile))
      (should (map? (response-json :userProfile)))
      (let [profile (response-json :userProfile)]
        (should (= "qwer@example.com" (profile :email)))
        (should (= "Qwer" (profile :name)))
        (should (contains? profile :created))
        (should (string? (profile :created))))))

  (it "should fail when email already exists"
    (let [request-body
          "{\"email\":\"userone@example.com\",
            \"password\":\"password3\",
            \"name\": \"Qwer\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/user"
                               :request-method :post
                               :body request-body))
          response (:response request)]
      (should (= "text/plain"
                 (get (:headers response) "Content-Type")))
      (should (= (:status response) 403))
      (should (= "Forbidden." (response :body)))))

  (it "should fail when email is not valid"
    (let [request-body
          "{\"email\":\"dippitydoo\",
            \"password\":\"password3\",
            \"name\": \"Qwer\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/user"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (= (:status response) 400))
      (should (contains? response-json :errors))
      (should (map? (response-json :errors)))))

  (it "should fail when email is missing"
    (let [request-body
          "{\"password\":\"password3\",
            \"name\": \"Qwer\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/user"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (= (:status response) 400))
      (should (contains? response-json :errors))
      (should (map? (response-json :errors)))))

  (it "should fail when password is missing"
    (let [request-body
          "{\"email\":\"qwer2@example.com\",
            \"name\": \"Qwer2\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/user"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (= (:status response) 400))
      (should (contains? response-json :errors))
      (should (map? (response-json :errors)))))

  (it "should fail when name is missing"
    (let [request-body
          "{\"email\":\"qwer2@example.com\",
            \"password\": \"password2\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/user"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (= (:status response) 400))
      (should (contains? response-json :errors))
      (should (map? (response-json :errors))))))


(describe
  "user profile api, reads"

  (it "should not allow a profile to be read with no auth_token"
      (let [request (-> (session app)
                        (request "/api/user/userone@example.com"
                                 :request-method :get))
            response (:response request)]
        (should= 401 (response :status))
        (should-not= "application/json;charset=UTF-8"
                     (get (:headers response) "Content-Type"))))

  (it "should not allow a profile to be read when not the current user"
      (let [request (-> (session app)
                        (request "/api/user/usertwo@example.com"
                                 :request-method :get
                                 :headers {:auth_token
                                           util/good-token}))
            response (:response request)]
        (should= 401 (response :status))
        (should-not= "application/json;charset=UTF-8"
                     (get (:headers response) "Content-Type"))))

  (it "should return profile when auth_token is supplied and is user"
      (let [request (-> (session app)
                        (request "/api/user/userone@example.com"
                                 :request-method :get
                                 :headers {:auth_token
                                           util/good-token}))
            response (:response request)
            profile (parse-string (response :body) true)]
        (should= 200 (response :status))
        (should (map? profile))
        (should== [:email :name :created] (keys profile))
        (should= "userone@example.com" (profile :email))
        (should= "User One" (profile :name))
        (should-be string? (profile :created)))))


(describe
  "user profile api, writes"

  (it "should forbid an update without auth token"
      (let [request-body
            "{\"email\": \"userone@example.com\",
              \"name\": \"OTHER NAME\"}"
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/user/userone@example.com"
                                 :request-method :post
                                 :body request-body))
            response (request :response)]
        (should= 401 (response :status))
        (should-be string? (response :body))
        (should= "Not authorized." (response :body))))

  (it "should forbid an update to another users profile"
      (let [request-body
            "{\"email\": \"usertwo@example.com\",
              \"name\": \"OTHER NAME\"}"
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/user/usertwo@example.com"
                                 :request-method :post
                                 :body request-body
                                 :headers {:auth_token
                                           util/good-token}))
            response (request :response)]
        (should= 401 (response :status))
        (should-be string? (response :body))
        (should= "Not authorized." (response :body))))

  (it "should be an error if name is omitted"
      (let [request-body
            "{\"email\": \"userone@example.com\",
              \"zzz\": \"OTHER NAME\"}"
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/user/userone@example.com"
                                 :request-method :post
                                 :body request-body
                                 :headers {:auth_token
                                           util/good-token}))
            response (request :response)
            response-json (parse-string (response :body) true)]
        (should= 400 (response :status))
        (should-be map? response-json)
        (should-contain :errors response-json)
        (should== {:name ["is invalid" "can't be blank"]}
                  (response-json :errors))))

  (it "should be an error if email is omitted"
      (let [request-body
            "{\"lol\": \"userone@example.com\",
              \"name\": \"OTHER NAME\"}"
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/user/userone@example.com"
                                 :request-method :post
                                 :body request-body
                                 :headers {:auth_token
                                           util/good-token}))
            response (request :response)
            response-json (parse-string (response :body) true)]
        (should= 400 (response :status))
        (should-be map? response-json)
        (should-contain :errors response-json)
        (should== {:email ["can't be blank"]} (response-json :errors))))

    (it "should be an error if name is not a string"
      (let [request-body
            "{\"email\": \"userone@example.com\",
              \"name\": 42}"
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/user/userone@example.com"
                                 :request-method :post
                                 :body request-body
                                 :headers {:auth_token
                                           util/good-token}))
            response (request :response)
            response-json (parse-string (response :body) true)]
        (should= 400 (response :status))
        (should-be map? response-json)
        (should-contain :errors response-json)
        (should== {:name ["is invalid"]} (response-json :errors))))

  (it "should update profile to new values with good auth token"
      (let [old-profile (user/get-profile "userone@example.com")
            request-body
            "{\"email\": \"userone@example.com\",
              \"name\": \"OTHER NAME\"}"
            request (-> (session app)
                        (content-type "application/json")
                        (request "/api/user/userone@example.com"
                                 :request-method :post
                                 :body request-body
                                 :headers {:auth_token
                                           util/good-token}))
            response (request :response)
            response-json (parse-string (response :body) true)]
        (should= 200 (response :status))
        (should-be map? response-json)
        (should-not-contain :errors response-json)
        (should-contain :email response-json)
        (should-contain :name response-json)
        (should-contain :created response-json)
        (should= "userone@example.com" (response-json :email))
        (should= "OTHER NAME" (response-json :name))
        (should-not= (old-profile :name) (response-json :name)))))


(run-specs)
