(ns todo.core
  (:require [datomic.client.api :as d]))

(defn get-tasks
  "Get all tasks on the to-do list"
  [list-name])

(defn add-task
  "Add a task to the to-do list. Accepts a string describing the task."
  [list-name task])

(defn get-task
  "Get single task by id."
  [list-name task-id])

(defn remove-task
  "Removes a task from the to-do list. Accepts the id of the task to remove."
  [list-name task-id])

(defn mark-complete
  "Marks a task incomplete. Accepts task-id."
  [list-name task-id])

(defn mark-incomplete
  "Marks a task incomplete. Accepts task-id."
  [list-name task-id])