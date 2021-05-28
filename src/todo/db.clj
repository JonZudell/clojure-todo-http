(ns todo.db
  (:require [datomic.client.api :as d]))

(defonce conn nil)
(def client (d/client {:server-type :dev-local
                       :system "todo-tasks"
                       :storage-dir :mem}))
;; datomic setup
(defn create-db []
  (let [schema (read-string (slurp "resources/schema.edn"))
        _ (d/create-database client {:db-name  "todo-tasks"})
        conn (d/connect client {:db-name "todo-tasks"})]
    (d/transact conn schema)))

(defn start
  []
  (alter-var-root #'conn (constantly (create-db)))) ;; alter-var-root is an atomic operation


(defn stop
  []
  (d/delete-database client {:db-name "todo-tasks"}))