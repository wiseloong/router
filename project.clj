(defproject wiseloong/router "0.1.0-SNAPSHOT"
  :description "wiseloong-前端-路由"
  :url "www.wiseloong.com"
  :license {:name "wiseloong"}

  :dependencies [[reagent "0.8.1"]
                 [bidi "2.1.5"]]

  :jar-exclusions [#"(?:^|\/)demo\/"]

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main       demo.core
                                       :asset-path "/out"
                                       :output-to  "target/cljsbuild/public/app.js"
                                       :preloads   [devtool.web]}}]}

  :profiles {:dev {:dependencies   [[org.clojure/clojure "1.9.0"]
                                    [org.clojure/clojurescript "1.10.439"]
                                    [wiseloong/devtool "1.1.0"]]
                   :resource-paths ["target/cljsbuild"]
                   :repl-options   {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}})
