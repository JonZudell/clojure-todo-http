(ns todo.handler
  (:require [todo.core :as tasks]
            [compojure.core :refer [routes wrap-routes GET PUT POST DELETE]]
            [compojure.route :as route]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
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
               (ring-response/charset "utf-8"))))))
(defn authorized? [request]
  (if (not (nil? (-> request :cookies :user)))
    true
    false))

(defn wrap-auth
  "If :cookies doesn't contain user."
  [handler]
  (fn [request]
    (if (authorized? request)
      (handler request)
      (-> (ring-response/response "Access Denied")
          (ring-response/status 403)))))

(def api-routes 
  (routes (GET "/api/tasks" [] {:body (tasks/get-tasks)})
          (POST "/api/tasks" {{task :task} :params} {:body (tasks/add-task task)})
          (DELETE "/api/tasks/:task-id" [task-id] {:body (tasks/remove-task (Integer/parseInt task-id))})
          (PUT "/api/tasks/:task-id/complete" [task-id] {:body (tasks/mark-complete (Integer/parseInt task-id))})
          (PUT "/api/tasks/:task-id/incomplete" [task-id] {:body (tasks/mark-incomplete (Integer/parseInt task-id))}))) ;; without the posibility to pass login this returns 404 because (/ 0 1) is not evaluated

(def non-api-routes 
  (routes (GET "/login" [] ()) ;; I'd prefer to raise a generic exception than to trigger an arithmatic error
          (GET "/api/server-error" [] (/ 0 1))
          (route/not-found "Not Found")))

(def app
     (-> (routes (wrap-routes api-routes
                              wrap-auth)
                 (-> non-api-routes)
                 (route/not-found "Not Found"))
         wrap-cookies
         wrap-500-catchall))
