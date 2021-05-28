(ns todo.handler
  (:require [todo.core :as core]
            [compojure.core :refer [routes wrap-routes GET PUT POST DELETE]]
            [compojure.route :as route]
            [ring.middleware.session :refer [wrap-session]]
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

(defn whoami [request] (-> request :session :user))
(def protected-routes 
  (routes (GET "/api/whoami" request  
            {:body (whoami request)})
          (GET "/api/tasks" 
            request {:body (core/get-tasks (whoami request))})
          (POST "/api/tasks" 
            request {:body (core/add-task (whoami request) 
                                           (-> request 
                                               :params
                                               :task))})
          (DELETE "/api/tasks/:task-id" 
            request {:body (core/remove-task (whoami request) 
                                              (Integer/parseInt (:task-id request)))})
          (PUT "/api/tasks/:task-id/complete" 
            request {:body (core/mark-complete (whoami request) 
                                                (Integer/parseInt (:task-id request)))})
          (PUT "/api/tasks/:task-id/incomplete" 
            request {:body (core/mark-incomplete (whoami request) 
                                                  (Integer/parseInt (:task-id request)))}))) ;; without the posibility to pass login this returns 404 because (/ 0 1) is not evaluated

(defn login [request]
  (let [user (get-in request [:params :user])
        session (get-in request [:session])]
     {:body "Success" :session (assoc session :user user)}))

(def unprotected-routes 
  (routes (POST "/login" request login) 
          (GET "/api/server-error" [] (/ 0 1)) ;; I'd prefer to raise a generic exception than to trigger an arithmatic error
          (route/not-found "Not Found")))

(defn authorized? [request]
  (if (not (nil? (-> request :session :user)))
    true
    false))

(defn wrap-auth
  "If :session doesn't contain user. Return Access Denied. Otherwise call next handler"
  [handler]
  (fn [request]
    (if (authorized? request)
      (handler request)
      (-> (ring-response/response "Access Denied")
          (ring-response/status 403)))))

(def app
     (-> (routes (-> protected-routes 
                     (wrap-routes wrap-auth)) ;; wrap-routes gotcha
                 unprotected-routes)
         wrap-json-response
         wrap-500-catchall
         wrap-session
         (wrap-defaults api-defaults)))