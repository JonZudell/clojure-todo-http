(ns todo.db
  (:require [datomic.client.api :as d]))

(def schema [{:db/ident :task/external-use-id
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "an id for external use"
              :db/unique :db.unique/identity}
             
             {:db/ident :task/user
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "user that authored the task"}

             {:db/ident :task/description
              :db/cardinality :db.cardinality/one
              :db/valueType :db.type/string
              :db/doc "description of the task"}

             {:db/ident :task/completed
              :db/cardinality :db.cardinality/one
              :db/valueType :db.type/boolean
              :db/doc "a boolean marking task completion"}
             
             {:db/ident :task/deleted
              :db/cardinality :db.cardinality/one
              :db/valueType :db.type/boolean
              :db/doc "a boolean marking task deletion"}])

(defonce conn nil)
(defonce client (d/client {:server-type :dev-local
                           :system "todo-tasks"
                           :storage-dir :mem}))
;; datomic setup
;; datomic.client.api will return true even if failed
;; https://ask.datomic.com/index.php/472/why-is-my-dev-local-db-not-found
(defn create-db []
  (let [_ (d/create-database client {:db-name  "todo-tasks"})
        conn (d/connect client {:db-name "todo-tasks"})]
    (d/transact conn {:tx-data schema})
    conn))

(defn start
  []
  (alter-var-root #'conn (constantly (create-db)))) ;; alter-var-root is an atomic operation

(defn stop
  []
  (d/delete-database client {:db-name "todo-tasks"}))

(defn fixture-setup [f]
  (start)
  (f)
  (stop))