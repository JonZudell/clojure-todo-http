(ns todo.handler-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [ring.mock.request :as mock]
            [todo.handler :as handler]
            [todo.core :as core]))

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
    (let [response (handler/app (mock/request :get "/login"))]
      (is (= (:status response) 200))
      (is (= (:body response) ()))))
  (testing "not-found route"
    (let [response (handler/app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest test-middleware
  (testing "catch exception return 500" 
    (let [response (handler/app (mock/request :get "/api/server-error"))]
      (is (= (:status response) 500))))
  (testing "login required" 
    (let [response (handler/app (mock/request :get "/api/tasks"))]
      (is (= (:status response) 403)))))

