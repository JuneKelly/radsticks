(defproject jetcan-server "0.1.1"
  :description
  "A toy app to demonstrate token based auth with clojure and angularjs"

  :url "http://github.com/ShaneKilkelly/jetcan-server"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [lib-noir "0.8.2"]
                 [compojure "1.1.6"]
                 [ring-server "0.3.1"]
                 [selmer "0.6.6"]
                 [com.taoensso/timbre "3.1.6"]
                 [com.postspectacular/rotor "0.1.0"]
                 [com.taoensso/tower "2.0.2"]
                 [markdown-clj "0.9.43"]
                 [liberator "0.11.0"]
                 [clj-time "0.7.0"]
                 [clj-jwt "0.0.6"]
                 [crypto-random "1.2.0"]
                 [com.novemberain/monger "1.7.0"]
                 [yesql "0.4.0"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [postgresql/postgresql "9.1-901-1.jdbc4"]
                 [com.novemberain/validateur "2.1.0"]
                 [migratus "0.7.0"]
                 [environ "0.5.0"]]

  :aot :all
  :repl-options {:init-ns jetcan-server.repl}

  :plugins [[lein-ring "0.8.7"]
            [lein-environ "0.5.0"]
            [migratus-lein "0.1.0"]
            [speclj "2.8.0"]]

  :ring {:handler jetcan-server.handler/app
         :init    jetcan-server.handler/init
         :destroy jetcan-server.handler/destroy}

  :profiles
  {:production
   {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}
    :env {:selmer-dev false
          :db-uri ""
          :db-user ""
          :db-password ""
          :secret ""}}

   :dev
   {:dependencies [[ring-mock "0.1.5"]
                   [peridot "0.2.2"]
                   [ring/ring-devel "1.2.2"]]
    :env {:selmer-dev true
          :db-uri "//localhost/jetcan"
          :db-user ""
          :db-password ""
          :secret "aterriblesecret"}
    :migratus {:store :database
               :migration-dir "sql/migrations"
               :db {:classname "org.postgresql.Driver"
                    :subprotocol "postgresql"
                    :subname "//localhost/jetcan"
                    :user ~(System/getenv "DB_USER")
                    :password ~(System/getenv "DB_PASSWORD")}}}

   :testing
   {:dependencies [[ring-mock "0.1.5"]
                   [peridot "0.2.2"]
                   [speclj "2.8.0"]
                   [ring/ring-devel "1.2.2"]]
    :ring {:port 3001}
    :env {:selmer-dev true
          :db-uri "//localhost/jetcan_test"
          :db-user ""
          :db-password ""
          :secret "aterriblesecret"}
    :migratus {:store :database
               :migration-dir "sql/migrations"
               :db {:classname "org.postgresql.Driver"
                    :subprotocol "postgresql"
                    :subname "//localhost/jetcan_test"
                    :user ~(System/getenv "DB_USER")
                    :password ~(System/getenv "DB_PASSWORD")}}}}

  :test-paths ["spec"]

  :min-lein-version "2.0.0")
