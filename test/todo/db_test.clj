(ns todo.db-test
  (:require [clojure.test :refer [use-fixtures deftest testing is]]
            [todo.db :as db]
            [datomic.client.api :as d]))

(use-fixtures :each db/fixture-setup)

(deftest test-get-tasks
  (testing "Load insert read in memory."
    (is (instance? datomic.dev_local.impl.MemoryConnection (db/start)))
    (d/transact db/conn {:tx-data db/schema})
    (d/transact db/conn {:tx-data [{:task/user "jon"
                                    :task/description "tax"
                                    :task/completed false}]})
    (is (= [1]  (first (d/q '[:find (count ?t)
                              :where [?t :task/user "jon"]]
                            (d/db db/conn)))))
    (is (= nil (first(d/q '[:find (count ?t)
                            :where [?t :task/user "stebe"]]
                          (d/db db/conn)))))
    (d/transact db/conn {:tx-data [{:task/user "stebe"
                                    :task/description "sbuba"
                                    :task/completed false}]})
    (is (= [1]  (first(d/q '[:find (count ?t)
                             :where [?t :task/user "stebe"]]
                           (d/db db/conn)))))))