(ns todo.requirements-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.handler :refer [app]]
            [peridot.core :refer [request session]]
            [cheshire.core :as json]
            [todo.db :as db]))

(use-fixtures :each db/fixture-setup)

(deftest test-requirements
  (testing "steve logs in and creates two tasks"
    (let [response (:response (-> (session app)
                                  (request "/login" ;; "Log in" with an email address
                                           :request-method :post
                                           :params {:user "steve@hotmail.com"})
                                  (request "/api/tasks" ;; Add a new to-do task
                                           :request-method :post
                                           :params {:task "tax 1"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "tax 2"})
                                  (request "/api/tasks")))]
      (is (= 2 (count (json/parse-string (:body response)))))))
  (testing "bob logs in and creates one task"
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob@gmail.com"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "garbage 1"})
                                  (request "/api/tasks")))]
      (is (= 1 (count (json/parse-string (:body response)))))))
  (testing "steve bob exclusive" ;; Unique todo-list on a per user basis
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "steve@hotmail.com"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "tax 2"})
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob@gmail.com"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "garbage 2"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "garbage 3"})
                                  (request "/api/tasks")))]
      (is (= 3 (count (json/parse-string (:body response)))))))
  (testing "bob completes a task" ;; complete vs. incomplete
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob@gmail.com"})
                                  (request "/api/tasks/incomplete-count"
                                           :request-method :get)))]
      (is (= 1 (count (json/parse-string (:body response))))))
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob@gmail.com"})
                                  (request "/api/tasks/complete-count"
                                           :request-method :get)))]
      (is (= 0 (count (json/parse-string (:body response))))))
    (let [response (:response (-> (session app)
                                  (request "/login"
                                           :request-method :post
                                           :params {:user "bob@gmail.com"})
                                  (request "/api/tasks"
                                           :request-method :post
                                           :params {:task "garbage 2"})))]
      (let [response2 (:response (-> (session app)
                                     (request "/login"
                                              :request-method :post
                                              :params {:user "bob@gmail.com"})
                                     (request (str "/api/tasks/" ((json/parse-string (:body response)) "external-use-id") "/complete")
                                              :request-method :put)))]
        (println (str "/api/tasks/" ((json/parse-string (:body response)) "external-use-id") "/complete"))
        (print response2))
      )))

;;(str "/api/tasks/" ((json/parse-string (:body response)) "external-use-id") "/complete")