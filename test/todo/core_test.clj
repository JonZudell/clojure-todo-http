(ns todo.core-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.core :as core]
            [todo.db :as db]))

(use-fixtures :each db/fixture-setup)

(deftest test-get-tasks
  (testing "No tasks."
    (is (empty? (core/get-tasks "bob"))))
  (testing "Add/Remove Tasks"
    (core/remove-task (core/add-task "bob" "Task One"))
    (is (= 0 (count (core/get-tasks "bob")))))
  (testing "Add Tasks"
    (core/add-task "steve" "scuba")
    (is (= 1 (count (core/get-tasks "steve")))))
  (testing "Get Task"
    (is (not (nil? (core/get-task (core/add-task "bob" "Task One")))))))

(deftest test-mark-completion
  (testing "Test Complete"
    (is (:task/completed
         (core/get-task
          (core/mark-complete
           (core/add-task "bob" "Task One"))))))
  (testing "Test Incomplete"
    (is (not 
         (:task/completed
          (core/get-task
           (core/mark-incomplete
            (core/mark-complete
             (core/add-task "bob" "Task One")))))))))