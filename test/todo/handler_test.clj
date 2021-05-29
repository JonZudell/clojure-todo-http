(ns todo.handler-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.handler :refer [app]]
            [peridot.core :refer [request session]]
            [cheshire.core :as json]
            [todo.db :as db]))

(use-fixtures :each db/fixture-setup)

(deftest test-app
  (testing "main route"
    (let [response (:response (-> (session app)
                                  (request "/login" 
                                           :request-method :post 
                                           :params {:user "bob"})
                                  (request "/api/whoami")))]
      (is (= (:status response) 200))
      (is (= (:body response) "bob"))
      (not (nil? (:session response)))))
  
  (testing "not-found route"
    (let [response (:response (-> (session app)
                                  (request "/invalid")))]
      (is (= (:status response) 404)))))

(deftest test-middleware
  (testing "should catch exception return 500"
    (let [response (:response (-> (session app)
                                  (request "/api/server-error")))]
      (is (= (:status response) 500))))

  (testing "should return 403"
    (let [response (:response (-> (session app)
                                  (request "/api/tasks"
                                           :request-method :get)))]
      (is (= (:status response) 403))))

  (testing "should be able to create task"
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "tax"})))]
      (is (= (:status response) 200)))))

(deftest test-unique-for-user
  (testing "steves tasks"
    (let [response (:response (-> (session app)
                                  (request "/login" 
                                           :request-method :post 
                                           :params {:user "steve"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "tax 1"})
                                  (request "/api/tasks")))]
      (is (= {"1" {"task" "tax 1"}} 
             (json/parse-string (:body response))))))
  (testing "bobs tasks"
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "garbage 1"})
                                  (request "/api/tasks")))]
      (is (= {"2" {"task" "garbage 1"}} 
             (json/parse-string (:body response))))))
  (testing "steve bob exclusive"
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "steve"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "tax 2"})
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "garbage 2"})
                                  (request "/api/tasks")))]
      (is (= {"2" {"task" "garbage 1"} "4" {"task" "garbage 2"}} 
             (json/parse-string (:body response)))))))