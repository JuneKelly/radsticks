(ns jetcan.api-auth-spec
  (:require [jetcan.test-utils :as util]
            [speclj.core :refer :all]
            [peridot.core :refer :all]
            [jetcan.handler :refer :all]
            [jetcan.db.user :as user]
            [cheshire.core :refer [generate-string
                                   parse-string]]))

(describe
  "auth api"

  (before
   (do (util/reset-db!)
       (util/populate-users!)))

  (it "should issue a token when credentials are correct"
    (let [request-body
          "{\"email\":\"userone@example.com\",
            \"password\":\"password1\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/auth"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (= (:status response) 201))
      (should (contains? response-json :token))
      (should (string? (response-json :token)))
      (should (< 0 (count (response-json :token))))
      (should (= "userone@example.com" (response-json :email)))))

  (it "should fail to authenticate when email is unknown"
    (let [request-body
          "{\"email\":\"gooser@example.com\",
            \"password\":\"lol\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/auth"
                               :request-method :post
                               :body request-body))
          response (:response request)]
      (should (= "text/plain"
                 (get (:headers response) "Content-Type")))
      (should (not (= (:status response) 201)))
      (should (= (:status response) 403))
      (should (= "Forbidden." (response :body)))))

  (it "should fail to authenticate when password is incorrect"
    (let [request-body
          "{\"email\":\"userone@example.com\",
            \"password\":\"lol\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/auth"
                               :request-method :post
                               :body request-body))
          response (:response request)]
      (should (= "text/plain"
                 (get (:headers response) "Content-Type")))
      (should (not (= (:status response) 201)))
      (should (= (:status response) 403))
      (should (= "Forbidden." (response :body)))))

  (it "should fail when user email is not submitted"
    (let [request-body
          "{\"derp\":\"userone@example.com\",
            \"password\":\"password1\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/auth"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (not (= (:status response) 201)))
      (should (= (:status response) 400))
      (should (not (contains? response-json :token)))
      (should (contains? response-json :errors))
      (should (map? (response-json :errors)))
      (should== {:email ["is invalid" "can't be blank"]}
                (:errors response-json))))

  (it "should fail when password is not submitted"
    (let [request-body
          "{\"email\":\"userone@example.com\",
            \"derp\":\"password1\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/auth"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (not (= (:status response) 201)))
      (should (= (:status response) 400))
      (should (not (contains? response-json :token)))
      (should (contains? response-json :errors))
      (should (map? (response-json :errors)))
      (should== {:password ["is invalid" "can't be blank"]}
                (:errors response-json))))

  (it "should fail when the supplied email is not a string"
    (let [request-body
          "{\"email\":[1,2,3],
            \"password\":\"password1\"}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/auth"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (not (= (:status response) 201)))
      (should (= (:status response) 400))
      (should (not (contains? response-json :token)))
      (should (contains? response-json :errors))
      (should (map? (response-json :errors)))
      (should== {:email ["is invalid"]}
                (:errors response-json))))

  (it "should fail when password is not a string"
    (let [request-body
          "{\"email\":\"userone@example.com\",
            \"password\":true}"
          request (-> (session app)
                      (content-type "application/json")
                      (request "/api/auth"
                               :request-method :post
                               :body request-body))
          response (:response request)
          response-json (parse-string (response :body) true)]
      (should (= "application/json;charset=UTF-8"
                 (get (:headers response) "Content-Type")))
      (should (not (= (:status response) 201)))
      (should (= (:status response) 400))
      (should (not (contains? response-json :token)))
      (should (contains? response-json :errors))
      (should (map? (response-json :errors)))
      (should== {:password ["is invalid"]}
                (:errors response-json)))))


(run-specs)
