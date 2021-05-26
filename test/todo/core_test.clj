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
    (is (empty? (tasks/get-tasks "bob"))))
  (testing "Add/Remove Tasks"
    (tasks/add-task "bob" "Task One")
    (is (= 1 (count (tasks/get-tasks "bob"))))
    (tasks/add-task "steve" "Task Two")
    (is (= 1 (count (tasks/get-tasks "bob"))))
    (tasks/remove-task "bob" 1)
    (is (= 0 (count (tasks/get-tasks "bob"))))
    (is(= ["bob" "steve"](tasks/get-task-lists)))))

(deftest test-mark-completion
  (testing "Test Complete"
    (tasks/add-task "bob" "Task One")
    (tasks/mark-complete "bob" 1)
    (is (= {:task "Task One" :complete true} (tasks/get-task "bob" 1))))
  (testing "Test Incomplete"
    (tasks/mark-incomplete "bob" 1)
    (is (= {:task "Task One" :complete false} (tasks/get-task "bob" 1)))))