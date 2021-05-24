(ns chapter2.core-test
  (:require [clojure.test :refer :all]
            [chapter2.core :refer :all]))

(defn clear-tasks-fixture [f] (swap! tasks empty) (f))
(use-fixtures :each clear-tasks-fixture)

(deftest test-get-tasks
  (testing "No tasks."
    (is (empty? (get-tasks))))
  (testing "Add Tasks"
    (add-task "Task One")
    (is (= 1 (count (get-tasks))))))
