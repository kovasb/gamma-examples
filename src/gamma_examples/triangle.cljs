(ns gamma-examples.triangle
  (:require [gamma.api :as g]
            [gamma.program :as p]
            [gamma-driver.drivers.basic :as driver]
            [gamma-driver.protocols :as dp]
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
  (let [d (example-driver)]
    (driver/draw-program
      d
      (dp/program d (example-program))
      (example-data))))

