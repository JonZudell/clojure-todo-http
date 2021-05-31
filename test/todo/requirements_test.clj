(ns todo.requirements-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.handler :refer [app]]
            [peridot.core :refer [request session]]
            [cheshire.core :as json]
            [todo.db :as db]))

(use-fixtures :each db/fixture-setup)

(deftest test-requirements
  (testing "steve logs in and creates two tasks"
    (is (= 2 (-> (session app)
                 (request "/login" ;; "Log in" with an email address
                          :request-method :post
                          :params {:user "steve@hotmail.com"})
                 (request "/api/tasks" ;; Add a new to-do task
                          :request-method :post
                          :params {:task "tax 1"})
                 (request "/api/tasks" ;; Add a new to-do task
                          :request-method :post
                          :params {:task "tax 2"})
                 (request "/api/tasks") ;; Get All tasks for user
                 :response
                 :body
                 json/parse-string
                 count))))
  (testing "bob logs in and creates one task"
      (is (= 1 (-> (session app)
                   (request "/login"
                            :request-method :post
                            :params {:user "bob@gmail.com"})
                   (request "/api/tasks"
                            :request-method :post
                            :params {:task "garbage 1"})
                   (request "/api/tasks")
                   :response
                   :body
                   json/parse-string
                   count))))
  (testing "steve bob exclusive" ;; Unique todo-list on a per user basis
      (is (= 3 (-> (session app)
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
                   (request "/api/tasks")
                   :response
                   :body
                   json/parse-string
                   count))))
  (testing "bob completes a task" ;; complete vs. incomplete
    (is (= 1 (-> (session app)
                 (request "/login"
                          :request-method :post
                          :params {:user "bob@gmail.com"})
                 (request "/api/tasks/incomplete-count"
                          :request-method :get)
                 :response
                 :body
                 json/parse-string
                 count)))
    (is (= 0 (-> (session app)
                 (request "/login"
                          :request-method :post
                          :params {:user "bob@gmail.com"})
                 (request "/api/tasks/complete-count"
                          :request-method :get)
                 :response
                 :body
                 json/parse-string
                 count)))
    (let [external-use-id ((-> (session app)
                               (request "/login"
                                        :request-method :post
                                        :params {:user "bob@gmail.com"})
                               (request "/api/tasks"
                                        :request-method :post
                                        :params {:task "garbage 2"})
                               :response
                               :body
                               json/parse-string)
                           "external-use-id")]
      (-> (session app)
          (request "/login"
                   :request-method :post
                   :params {:user "bob@gmail.com"})
          (request (str "/api/tasks/" external-use-id "/complete")
                   :request-method :put))
      (is (= 1 (-> (session app)
                   (request "/login"
                            :request-method :post
                            :params {:user "bob@gmail.com"})
                   (request "/api/tasks/complete-count"
                            :request-method :get)
                   :response
                   :body
                   json/parse-string
                   first)))
      (-> (session app)
          (request "/login"
                   :request-method :post
                   :params {:user "bob@gmail.com"})
          (request (str "/api/tasks/" external-use-id "/incomplete")
                   :request-method :put))
      (is (= nil (-> (session app) ;; counts endpoints are returning nil when no tasks
                     (request "/login"
                              :request-method :post
                              :params {:user "bob@gmail.com"})
                     (request "/api/tasks/complete-count"
                              :request-method :get)
                     :response
                     :body
                     json/parse-string
                     first)))
      (is (= 3 (-> (session app) ;; incomplete count
                   (request "/login"
                            :request-method :post
                            :params {:user "steve@hotmail.com"})
                   (request "/api/tasks/incomplete-count"
                            :request-method :get)
                   :response
                   :body
                   json/parse-string
                   first)))
      (is (= 3 (-> (session app) ;; History of changes for burndown chart
                   (request "/login"
                            :request-method :post
                            :params {:user "steve@hotmail.com"})
                   (request "/api/tasks/complete-history"
                            :request-method :get)
                   :response
                   :body
                   json/parse-string
                   count)))
      (is (= 6 (-> (session app) ;; History of changes for burndown chart
                   (request "/login"
                            :request-method :post
                            :params {:user "bob@gmail.com"})
                   (request "/api/tasks/complete-history"
                            :request-method :get)
                   :response
                   :body
                   json/parse-string
                   count)))
      (-> (session app)
          (request "/login"
                   :request-method :post
                   :params {:user "bob@gmail.com"})
          (request (str "/api/tasks/" external-use-id "/incomplete")
                   :request-method :put))
      (is (= 6 (-> (session app) ;; Won't increment if already incomplete
                   (request "/login"
                            :request-method :post
                            :params {:user "bob@gmail.com"})
                   (request "/api/tasks/complete-history"
                            :request-method :get)
                   :response
                   :body
                   json/parse-string
                   count)))
      (-> (session app)
          (request "/login"
                   :request-method :post
                   :params {:user "bob@gmail.com"})
          (request (str "/api/tasks/" external-use-id "/complete")
                   :request-method :put))
      (is (= 7 (-> (session app) ;; Won't increment if already complete
                   (request "/login"
                            :request-method :post
                            :params {:user "bob@gmail.com"})
                   (request "/api/tasks/complete-history"
                            :request-method :get)
                   :response
                   :body
                   json/parse-string
                   count)))
      (-> (session app)
          (request "/login"
                   :request-method :post
                   :params {:user "bob@gmail.com"})
          (request (str "/api/tasks/" external-use-id "/delete")
                   :request-method :put))
      (is (= 7 (-> (session app) ;; Won't increment if already completed
                   (request "/login"
                            :request-method :post
                            :params {:user "bob@gmail.com"})
                   (request "/api/tasks/complete-history"
                            :request-method :get)
                   :response
                   :body
                   json/parse-string
                   count))))))
