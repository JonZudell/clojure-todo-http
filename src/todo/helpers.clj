(ns todo.helpers
  (:require [ring.util.response :as ring-response]
            [todo.db :as db]))
(defn whoami [request] (-> request :session :user))

(defn login [request]
  (let [user (get-in request [:params :user])
        session (get-in request [:session])]
    {:body "Success" :session (assoc session :user user)}))

(defn authorized? [request]
  (if (not (nil? (-> request :session :user)))
    true
    false))

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

(defn wrap-auth
  "If :session doesn't contain user. Return Access Denied. Otherwise call next handler"
  [handler]
  (fn [request]
    (if (authorized? request)
      (handler request)
      (-> (ring-response/response "Access Denied")
          (ring-response/status 403)))))

(defn wrap-conn
  "make sure a connection is made"
  [handler]
  (fn [request]
    do (db/start) (handler request)))