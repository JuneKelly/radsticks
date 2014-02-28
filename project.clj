(defproject radsticks "0.1.0"
  :description
  "A toy app to demonstrate token based auth with clojure and angularjs"

  :url "http://github.com/ShaneKilkelly/radsticks"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.8.1"]
                 [compojure "1.1.6"]
                 [ring-server "0.3.1"]
                 [selmer "0.5.8"]
                 [com.taoensso/timbre "2.7.1"]
                 [com.postspectacular/rotor "0.1.0"]
                 [com.taoensso/tower "2.0.1"]
                 [markdown-clj "0.9.40"]
                 [liberator "0.10.0"]
                 [clj-time "0.6.0"]
                 [clj-jwt "0.0.4"]
                 [crypto-random "1.2.0"]
                 [com.novemberain/monger "1.7.0"]
                 [environ "0.4.0"]]

  :aot :all
  :repl-options {:init-ns radsticks.repl}

  :plugins [[lein-ring "0.8.7"]
            [lein-environ "0.4.0"]
            [speclj "2.8.0"]]

  :ring {:handler radsticks.handler/app
         :init    radsticks.handler/init
         :destroy radsticks.handler/destroy}

  :profiles
  {:production
   {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}
    :env {:selmer-dev false}}

   :dev
   {:dependencies [[ring-mock "0.1.5"]
                   [peridot "0.2.2"]
                   [ring/ring-devel "1.2.1"]]
    :env {:selmer-dev true
          :db-uri "mongodb://localhost/radsticks"
          :secret "aterriblesecret"}}

   :testing
   {:dependencies [[ring-mock "0.1.5"]
                   [peridot "0.2.2"]
                   [speclj "2.8.0"]
                   [ring/ring-devel "1.2.1"]]
    :ring {:port 3001}
    :env {:selmer-dev true
          :db-uri "mongodb://localhost/radsticks_test"
          :secret "aterriblesecret"}}}

  :test-paths ["spec"]

  :min-lein-version "2.0.0")
