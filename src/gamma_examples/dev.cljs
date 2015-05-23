(ns gamma-examples.dev
  (:require [clojure.browser.repl :as repl]
            gamma-examples.triangle))

(defonce conn
         (repl/connect "http://localhost:9000/repl"))

(gamma-examples.triangle/main)

(comment
  (require 'gamma-examples.triangle-gradient)
  (in-ns 'gamma-examples.triangle-gradient)
  (main)
  (require 'gamma-examples.mvp)
  (in-ns 'gamma-examples.mvp)
  (main)
  (require 'gamma-examples.cuboid1)
  (in-ns 'gamma-examples.cuboid1)
  (main)
  (require 'gamma-examples.cuboid2)
  (in-ns 'gamma-examples.cuboid2)
  (main)
  (require 'gamma-examples.texture)
  (in-ns 'gamma-examples.texture)
  (main)
  (require 'gamma-examples.animate)
  (in-ns 'gamma-examples.animate)
  (main)
  )
