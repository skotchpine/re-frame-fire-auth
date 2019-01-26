(defproject re-frame-fire-auth "0.0.2"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.6"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.18"]]

  :min-lein-version "2.5.3"

  :figwheel {:css-dirs ["resources/public/css"]}

  :cljsbuild
  {:builds
   [{:id "dev"
     :source-paths ["src"]
     :figwheel {:on-jsload "re-frame-fire-auth.core/mount"}
     :compiler {:main re-frame-fire-auth.core
                :output-to "resources/public/js/compiled/app.js"
                :output-dir "resources/public/js/compiled/out"
                :asset-path "/js/compiled/out"
                :source-map-timestamp true
                :closure-defines {goog.DEBUG true
                                  process.env/NODE_ENV "development"}
                :preloads [devtools.preload]
                :external-config {:devtools/config {:features-to-install :all}}}}

    {:id "min"
     :source-paths ["src"]
     :compiler {:main re-frame-fire-auth.core
                :output-to "resources/public/js/compiled/app.js"
                :optimizations :advanced
                :closure-defines {goog.DEBUG false
                                  process.env/NODE_ENV "production"}
                :pretty-print false}}]}

  :aliases {"package" ["do"
                       ["clean"]
                       ["cljsbuild" "once" "min"]]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.0"]
                   [figwheel-sidecar "0.5.18"]
                   [cider/piggieback "0.3.10"]]

    :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
    :source-paths ["src" "dev"]
    :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}})
