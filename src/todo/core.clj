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

(defn get-complete-counts
  "Get a map showing counts of (complete or deleted )/incomplete tasks."
  [user]
  (first (d/q '[:find (count ?complete)
                :with ?c
                :in $ ?user
                :where [?c :task/user ?user]
                [?c :task/completed true]
                [?c :task/completed ?complete]]
       (d/db db/conn) user)))

(defn get-incomplete-counts
  "Get a map showing counts of (complete or deleted )/incomplete tasks."
  [user]
  (first (d/q '[:find (count ?incomplete)
                :with ?i
                :in $ ?user
                :where [?i :task/user ?user]
                [?i :task/completed false]
                [?i :task/completed ?incomplete]]
              (d/db db/conn) user)))


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

;; To simplify some stuff I've decided to complete the task upon deletion
;; If we want to "undelete" we can see what the stated of completed was.
;; The transaction that deleted it on execution should contain a retration
;; for a :task/completed false fact.
(defn remove-task
  "Removes a task from the to-do list. Accepts the id of the task to remove."
  [external-use-id]
  (d/transact db/conn 
              {:tx-data [{:task/external-use-id external-use-id
                          :task/deleted true
                          :task/completed true}]})
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

(defn complete-history
  "returns the history of changes to the :task/completed attribute
   for all task entities belonging to a user"
  [user]
  (sort (d/q '[:find ?tx-time ?e ?v ?confirmed-user
               :in $ ?user ?a
               :where [?e ?a ?v ?tx true]
               [?tx :db/txInstant ?tx-time]
               [?e :task/user ?user]
               [?e :task/user ?confirmed-user]]
             (d/history (d/db db/conn))
             user
             :task/completed)))