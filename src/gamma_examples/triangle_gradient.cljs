(ns gamma-examples.triangle-gradient
  (:require [gamma.api :as g]
            [gamma.program :as p]
            [gamma-driver.api :as gd]
            [gamma-driver.drivers.basic :as driver]
            [goog.dom :as gdom]
            [goog.webgl :as ggl]))



(def pos-attribute (g/attribute "posAttr" :vec2))

(def color-attribute (g/attribute "colorAttr" :vec4))

(def color-varying (g/varying "colorVarying" :vec4 :mediump))

(defn example-program []
  (p/program
    {:vertex-shader {(g/gl-position) (g/vec4 pos-attribute 0 1)
                     color-varying color-attribute}
     :fragment-shader {(g/gl-frag-color) color-varying}}))


(defn example-data []
  {pos-attribute   [[-0.5 -0.5] [0.5 -0.5] [0 0]]
   color-attribute [[1 0 0 1] [0 1 0 1] [0 0 1 1]]})


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