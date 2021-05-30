(ns todo.core (:require [datomic.client.api :as d]
                        [todo.db :as db]))
(defn get-task 
  "Get a single task by external use id"
  [external-use-id]
  (-> (d/q '[:find (pull ?t [*])
             :in $ ?external-use-id
             :where [?t :task/external-use-id ?external-use-id]]
           (d/db db/conn) external-use-id)
      first
      first))

(defn get-tasks
  "Get all tasks for a user."
  [user]
  (d/q '[:find (pull ?t [:task/user :task/external-use-id :task/description :task/completed])
         :in $ ?user
         :where [?t :task/user ?user]
         [?t :task/description ?description]
         [?t :task/completed ?completed]
         [?t :task/deleted false]]

       (d/db db/conn) user))

(defn add-task
  "Add a task to Datomic."
  [user description] ;; Can't use squuid
  (let [external-use-id (.toString (java.util.UUID/randomUUID))]
    (d/transact db/conn
                {:tx-data [{:task/user user
                            :task/external-use-id external-use-id
                            :task/description description
                            :task/completed false
                            :task/deleted false}]})
    external-use-id))

(defn remove-task
  "Removes a task from the to-do list. Accepts the id of the task to remove."
  [external-use-id]
  (d/transact db/conn 
              {:tx-data [{:task/external-use-id external-use-id
                          :task/deleted true}]})
  external-use-id)

(defn mark-complete
  "Marks a task complete. Accepts task-id."
  [external-use-id]
  (d/transact db/conn
              {:tx-data [{:task/external-use-id external-use-id
                          :task/completed true}]})
  external-use-id)

(defn mark-incomplete
  "Marks a task incomplete. Accepts task-id."
  [external-use-id]
  (d/transact db/conn
              {:tx-data [{:task/external-use-id external-use-id
                          :task/completed false}]})
  external-use-id)