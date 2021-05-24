(ns chapter2.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [chapter2.handler :refer :all]
            [chapter2.core :refer :all]))

(defn clear-tasks-fixture [f] (swap! tasks empty) (f))
(use-fixtures :each clear-tasks-fixture)

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/api/tasks"))]
      (is (= (:status response) 200))
      (is (= (:body response) "{}"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
