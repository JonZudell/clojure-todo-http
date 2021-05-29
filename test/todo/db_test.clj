(ns todo.db-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.db :as db]
            [datomic.client.api :as d]))

(defn clear-tasks-fixture [f]
  (db/start)
  (f)
  (db/stop))
(use-fixtures :each clear-tasks-fixture)

(deftest test-get-tasks
  (testing "No tasks."
    (is (instance? datomic.dev_local.impl.MemoryConnection (db/start)))
    (is (d/transact db/conn db/schema))))

