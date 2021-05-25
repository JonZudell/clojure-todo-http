(ns todo.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [todo.handler :refer :all]
            [todo.core :refer :all]))

(defn reset-long
  [n]
  (- n n))
(defn clear-tasks-fixture [f]
  (swap! tasks empty)
  (swap! id-atom reset-long)
  (f))
(use-fixtures :each clear-tasks-fixture)

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/api/tasks"))]
      (is (= (:status response) 200))
      (is (= (:body response) "{}"))))
  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest test-middleware
  (testing "catch exception return 500" 
    (let [response (app (mock/request :get "/api/server-error"))]
      (is (= (:status response) 500)))))
