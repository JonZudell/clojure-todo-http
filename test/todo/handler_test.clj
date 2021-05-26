(ns todo.handler-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.handler :refer [app]]
            [todo.core :as core]
            [peridot.core :refer [request session]]))

(defn reset-long
  [n]
  (- n n))
(defn clear-tasks-fixture [f]
  (swap! core/tasks empty)
  (swap! core/id-atom reset-long)
  (f))
(use-fixtures :each clear-tasks-fixture)

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
  (testing "catch exception return 500" 
    (let [response (:response (-> (session app)
                                  (request "/api/server-error")))]
      (is (= (:status response) 500))))
  (testing "login required" 
    (let [response (:response (-> (session app)
                                  (request "/api/tasks")))]
      (is (= (:status response) 403)))))