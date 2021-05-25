(ns todo.handler
  (:require [todo.core :as tasks]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :as ring-response]))

(defn wrap-500-catchall
  "Wrap the given handler in a try/catch expression, returning a 500 response if any exceptions are caught."
  [handler] ;; middlewares take and return handler functions.
  (fn [request] ;; handler functions take requests and return responses
    (try (handler request)
         (catch Exception e 
           (-> (ring-response/response (.getMessage e))
               (ring-response/status 500)
               (ring-response/content-type "text/plain")
               (ring-response/charset "utf-8"))))
    )
  )

(defroutes api-routes
  (GET "/api/tasks" [] 
    {:body (tasks/get-tasks)})
  (POST "/api/tasks" {{task :task} :params}
    {:body (tasks/add-task task)})
  (DELETE "/api/tasks/:task-id" [task-id]
    {:body (tasks/remove-task (Integer/parseInt task-id))})
  (PUT "/api/tasks/:task-id/complete" [task-id]
    {:body (tasks/mark-complete (Integer/parseInt task-id))})
  (PUT "/api/tasks/:task-id/incomplete" [task-id]
    {:body (tasks/mark-incomplete (Integer/parseInt task-id))})
  (GET "/api/server-error" [] (/ 0 1)) ;; I'd prefer to raise a generic exception than to trigger an arithmatic error
  (route/not-found "Not Found"))

(def app 
  (wrap-500-catchall
   (-> api-routes
       (wrap-defaults api-defaults)
       wrap-json-response)))
