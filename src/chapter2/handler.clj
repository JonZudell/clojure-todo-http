(ns chapter2.handler
  (:require [chapter2.core :as tasks]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defroutes api-routes
  (GET "/api/tasks" [] 
    {:body (tasks/get-tasks)})
  (POST "/api/tasks" {{task :task} :params}
    {:body (tasks/add-task task)})
  (DELETE "/api/tasks/:task-id" [task-id]
    {:body (tasks/remove-task (Integer/parseInt task-id))})
  (route/not-found "Not Found"))

(def app 
  (-> api-routes
      (wrap-defaults api-defaults)
      wrap-json-response))
