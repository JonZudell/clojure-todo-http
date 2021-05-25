(ns chapter2.core)

(def id-atom (atom 0)); Atoms are mutable state
(defn next-id [] (swap! id-atom inc)); function that increments the value of the mutable id-atom

(def tasks (atom (sorted-map)))

(defn get-tasks
  "Get all tasks on the to-do list"
  []
  @tasks)

(defn add-task
  "Add a task to the to-do list. Accepts a string describing the task."
  [task]
  (swap! tasks assoc (next-id) task))

(defn remove-task
  "Removes a task from the to-do list. Accepts the id of the task to remove."
  [task-id]
  (swap! tasks dissoc task-id))