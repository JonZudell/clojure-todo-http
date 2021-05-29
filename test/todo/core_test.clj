(ns todo.core-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.core :as core]
            [todo.db :as db]))

(use-fixtures :each db/fixture-setup)

(deftest test-get-tasks
  (testing "No tasks."
    (is (empty? (core/get-tasks "bob"))))
  (testing "Add/Remove Tasks"
    (core/add-task "bob" "Task One")
    (is (= 1 (count (core/get-tasks "bob"))))
    (core/add-task "steve" "Task Two")
    (is (= 1 (count (core/get-tasks "bob"))))
    (core/remove-task "bob" 1)
    (is (= 0 (count (core/get-tasks "bob"))))))

;;(deftest test-mark-completion
;;  (testing "Test Complete"
;;    (core/add-task "bob" "Task One")
;;    (core/mark-complete "bob" 1)
;;    (is (= {:task "Task One" :complete true} (core/get-task "bob" 1))))
;;  (testing "Test Incomplete"
;;    (core/mark-incomplete "bob" 1)
;;    (is (= {:task "Task One" :complete false} (core/get-task "bob" 1)))))