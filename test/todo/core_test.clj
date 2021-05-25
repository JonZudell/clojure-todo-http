(ns todo.core-test
  (:require [clojure.test :refer :all]
            [todo.core :refer :all]))

(defn reset-long [n] (- n n))
(defn clear-tasks-fixture [f] 
  (swap! tasks empty)
  (swap! id-atom reset-long)
  (f))
(use-fixtures :each clear-tasks-fixture)

(deftest test-get-tasks
  (testing "No tasks."
    (is (empty? (get-tasks))))
  (testing "Add/Remove Tasks"
    (add-task "Task One")
    (is (= 1 (count (get-tasks))))
    (add-task "Task Two")
    (is (= 2 (count (get-tasks))))
    (remove-task 1)
    (is (= 1 (count (get-tasks))))))

(deftest test-mark-completion
  (testing "Test Complete"
    (add-task "Task One")
    (mark-complete 1)
    (is (= {:task "Task One" :complete true} (get-task 1))))
  (testing "Test Incomplete"
    (mark-incomplete 1)
    (is (= {:task "Task One" :complete false} (get-task 1)))))