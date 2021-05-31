(ns todo.handler-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.handler :refer [app]]
            [peridot.core :refer [request session]]
            [todo.db :as db]))

(use-fixtures :each db/fixture-setup)

(deftest test-app
  (testing "simple route tests"
    (let [response (:response (-> (session app)
                                  (request "/login" 
                                           :request-method :post 
                                           :params {:user "bob@gmail.com"})
                                  (request "/api/whoami")))]
      (is (= (:status response) 200))
      (is (= (:body response) "bob@gmail.com"))
      (not (nil? (:session response))))
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob@gmail.com"})
                                  (request "/api/tasks/complete-history")))]
      (is (= (:status response) 200))
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
                                           :params {:user "bob@gmail.com"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "tax"})))]
      (is (= (:status response) 200)))))

