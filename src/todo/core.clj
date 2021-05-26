(ns todo.core)

(def id-atom (atom 0)) ;; Atoms are mutable state
(defn next-id [] (swap! id-atom inc)) ;; function that increments the value of the mutable id-atom

(def tasks (atom (sorted-map)))

(defn get-task-lists
  "Get the keys for all created task lists."
  []
  (keys @tasks))

(defn get-tasks
  "Get all tasks on the to-do list"
  [list-name]
  (get @tasks list-name))

(defn add-task
  "Add a task to the to-do list. Accepts a string describing the task."
  [list-name task]
  (swap! tasks assoc-in [list-name (next-id)] {:task task}))

(defn get-task
  "Get single task by id."
  [list-name task-id]
  (get (get-tasks list-name) task-id))

(defn remove-task
  "Removes a task from the to-do list. Accepts the id of the task to remove."
  [list-name task-id]
  (swap! tasks update-in [list-name] dissoc task-id))

(defn mark-complete
  "Marks a task incomplete. Accepts task-id."
  [list-name task-id]
  (swap! tasks assoc-in [list-name task-id] (assoc (get-task list-name task-id) :complete true)))

(defn mark-incomplete
  "Marks a task incomplete. Accepts task-id."
  [list-name task-id]
  (swap! tasks assoc-in [list-name task-id] (assoc (get-task list-name task-id) :complete false)))