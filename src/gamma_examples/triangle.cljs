(ns gamma-examples.triangle
  (:require [gamma.api :as g]
            [gamma-driver.api :as gd]
            [gamma.program :as p]
            [gamma-driver.drivers.basic :as driver]
            [goog.dom :as gdom]
            [goog.webgl :as ggl]))

(def pos-attribute (g/attribute "posAttr" :vec2))

(defn example-program []
  (p/program
    {:vertex-shader {(g/gl-position) (g/vec4 pos-attribute 0 1)}
     :fragment-shader {(g/gl-frag-color) (g/vec4 1 0 0 1)}}))


(defn example-data []
  {pos-attribute [[-0.5 -0.5] [0.5 -0.5] [0 0]]})

(defn example-driver []
  (driver/basic-driver
    (.getContext (.getElementById js/document "gl-canvas") "webgl")))


(defn main []
  (let [driver (example-driver)
        program (example-program)
        data (example-data)]
    (gd/draw-arrays
      driver
      (gd/bind driver program data)
      {})))

(comment
  (def driver (example-driver))
  (def program (example-program))
  (def data (example-data))

  (def b1 (gd/bind driver program data))

  (@(:input-state driver) program)

  (gd/draw-arrays driver program {})



  (:input-state driver)


  )