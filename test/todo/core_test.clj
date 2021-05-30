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
             (core/add-task "bob" "Task Two"))))))))
  (testing "Test Counts"
    (is (= [1] (core/get-complete-counts "bob")))
    (is (= [1] (core/get-incomplete-counts "bob")))
    (core/mark-complete (core/add-task "bob" "Task Three"))
    (is (= [2] (core/get-complete-counts "bob")))
    (is (= [1] (core/get-incomplete-counts "bob")))
    (core/add-task "bob" "Task Four")
    (is (= [2] (core/get-incomplete-counts "bob")))))

;; Need to thread sleep or Datomic won't mark the history appropriately
;; Why is this a race condition?
(deftest test-completion-history
  (testing "History of changes"
    (let [external-use-id (core/add-task "bob" "Task One")]
      (Thread/sleep 5)
      (core/mark-complete external-use-id)
      (Thread/sleep 5)
      (core/mark-incomplete external-use-id))
    (let [external-use-id (core/add-task "steve" "Task Two")]
      (Thread/sleep 5)
      (core/mark-complete external-use-id)
      (Thread/sleep 5)
      (core/mark-incomplete external-use-id)
      (Thread/sleep 5)
      (core/mark-complete external-use-id)
    (is (= 3 (count (core/complete-history "bob"))))
    (is (= 4 (count (core/complete-history "steve")))))))