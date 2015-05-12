(ns gamma-examples.dev
  (:require [clojure.browser.repl :as repl]
            gamma-examples.triangle))

(defonce conn
         (repl/connect "http://localhost:9000/repl"))

(gamma-examples.triangle/main)