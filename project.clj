(defproject todo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :repositories {"cognitect-dev-tools"
                 {:url "https://dev-tools.cognitect.com/maven/releases/"
                  :creds :gpg}
                 "my.datomic.com"
                 {:url "https://my.datomic.com/repo"
                  :creds :gpg}}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.1"]
                 [com.datomic/dev-local "0.9.232"]]
  :plugins [[lein-ring "0.12.5"]
            [com.jakemccrary/lein-test-refresh "0.24.1"]]
  :ring {:handler todo.handler/app
         :nrepl {:start? true
                 :port 60000}}
  :main todo.core
  :profiles
  {:dev {:dependencies [[clj-http "3.12.0"]
                        [javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]
                        [peridot "0.5.3"]]}})
