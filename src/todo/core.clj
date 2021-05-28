(ns todo.core (:require [datomic.client.api :as d]
                        [todo.db :as db]))

(defn get-tasks
  "Get all tasks on the to-do list"
  [user])

(defn add-task
  "Add a task to the to-do list. Accepts a string describing the task."
  [user task])

(defn get-task
  "Get single task by id."
  [user task-id])

(defn remove-task
  "Removes a task from the to-do list. Accepts the id of the task to remove."
  [user task-id])

(defn mark-complete
  "Marks a task incomplete. Accepts task-id."
  [user task-id])

(defn mark-incomplete
  "Marks a task incomplete. Accepts task-id."
  [user task-id])