(require '[cljs.repl :as repl])
(require '[cljs.repl.browser])

(repl/repl*
  (cljs.repl.browser/repl-env*
    {:static-dir ["resources/public" "resources/public/js/out"]})
  {:output-dir "resources/public/js/out"
   :main 'gamma-examples.dev
   :asset-path "http://localhost:9000/js/out"})

(comment
  (require '[cljs.repl.server :as server])

  (server/dispatch-on :get
                      (fn [{:keys [path]} _ _]
                        (or
                          (= path "/")
                          (.endsWith path ".png")
                          (.endsWith path ".js")
                          (.endsWith path ".cljc")
                          (.endsWith path ".cljs")
                          (.endsWith path ".map")
                          (.endsWith path ".html")
                          (.endsWith path ".css")))
                      cljs.repl.browser/send-static))

