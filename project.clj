(defproject chapter2 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.1"]]
  :plugins [[lein-ring "0.12.5"]
            [com.jakemccrary/lein-test-refresh "0.24.1"]]
  :ring {:handler chapter2.handler/app
         :nrepl {:start? true
                 :port 60000}}
  :main chapter2.core
  :profiles
  {:dev {:dependencies [[clj-http "3.12.0"]
                        [javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
