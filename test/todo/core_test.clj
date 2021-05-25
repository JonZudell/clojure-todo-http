(ns todo.core-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.core :as tasks]))

(defn reset-long [n] (- n n))
(defn clear-tasks-fixture [f] 
  (swap! tasks/tasks empty)
  (swap! tasks/id-atom reset-long)
  (f))
(use-fixtures :each clear-tasks-fixture)

(deftest test-get-tasks
  (testing "No tasks."
    (is (empty? (tasks/get-tasks))))
  (testing "Add/Remove Tasks"
    (tasks/add-task "Task One")
    (is (= 1 (count (tasks/get-tasks))))
    (tasks/add-task "Task Two")
    (is (= 2 (count (tasks/get-tasks))))
    (tasks/remove-task 1)
    (is (= 1 (count (tasks/get-tasks))))))

(deftest test-mark-completion
  (testing "Test Complete"
    (tasks/add-task "Task One")
    (tasks/mark-complete 1)
    (is (= {:task "Task One" :complete true} (tasks/get-task 1))))
  (testing "Test Incomplete"
    (tasks/mark-incomplete 1)
    (is (= {:task "Task One" :complete false} (tasks/get-task 1)))))