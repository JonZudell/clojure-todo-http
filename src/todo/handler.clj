(ns todo.handler
  (:require [todo.core :as core]
            [todo.helpers :refer [login whoami wrap-500-catchall wrap-auth wrap-conn]]
            [compojure.core :refer [routes wrap-routes GET PUT POST DELETE]]
            [compojure.route :as route]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]))



(def unprotected-routes 
  (routes (POST "/login" request login)
          (GET "/api/server-error" [] (/ 0 1)) ;; I'd prefer to raise a generic exception than to trigger an arithmatic error
          (route/not-found "Not Found")))

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
                                                 (Integer/parseInt (:task-id request)))})))

(def app
     (-> (routes (-> protected-routes
                     (wrap-routes wrap-auth)
                     (wrap-routes wrap-conn)) ;; wrap-routes gotcha
                 unprotected-routes)
         wrap-json-response
         wrap-500-catchall
         wrap-session
         (wrap-defaults api-defaults)))