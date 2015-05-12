(require '[cljs.closure :as cljsc]
         '[cljs.build.api])

(cljsc/build (cljs.build.api/inputs "src")
             {:output-dir "resources/public/js/out"
              :output-to "resources/public/js/main.js"
              :main 'gamma-examples.dev
              :asset-path "http://localhost:9000/js/out"})