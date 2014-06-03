(ns jetcan.api-spec
  (:require [jetcan.test-utils :as util]
            [speclj.core :refer :all]
            [peridot.core :refer :all]
            [jetcan.handler :refer :all]
            [jetcan.db.user :as user]
            [cheshire.core :refer [generate-string
                                   parse-string]]))


(describe
  "main route"

  (it "should respond correctly on home route"
    (let [response (:response
                     (-> (session app)
                         (request "/")))]
      (should (= (response :status) 200))))

  (it "should return 404 on unknown route"
    (let [response (:response
                     (-> (session app)
                         (request "/invalid/route")))]
      (should (= (:status response) 404)))))


(run-specs)

