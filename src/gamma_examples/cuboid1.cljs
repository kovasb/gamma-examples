(ns gamma-examples.cuboid1
  (:require
    [gamma-driver.api :as gd]
    [gamma-driver.drivers.basic :as driver]
    [gamma.program :as p]
    [gamma.api :as g]
    [thi.ng.geom.core :as geom]
    [thi.ng.geom.core.matrix :as mat :refer [M44]]
    [thi.ng.geom.webgl.arrays :as arrays]
    [thi.ng.geom.basicmesh :as bm]
    [thi.ng.geom.cuboid :as cuboid]
    [thi.ng.geom.webgl.core :as tgl]))


(def pos-attribute (g/attribute "a_Pos" :vec3))

(def color-attribute (g/attribute "colorAttr" :vec4))

(def color-varying (g/varying "colorVarying" :vec4 :mediump))

(defn example-program []
  (p/program
    {:vertex-shader
     {(g/gl-position) (g/vec4 pos-attribute 1)
      color-varying   color-attribute}
     :fragment-shader
     {(g/gl-frag-color) color-varying}}))

(defn ->radians [degrees]
  (/ (* degrees Math/PI) 180))

(defn base-cuboid []
  (geom/rotate-around-axis
    (geom/rotate-around-axis
      (geom/scale (cuboid/cuboid) 0.5) [1 0 0] (->radians 30))
    [0 1 0] (->radians 20)))


(def face-colors
  ;; color per face
  ;; each face has 2 triangles; 6 vertices per face
  [(repeat 6 [1 0 0 1])
   (repeat 6 [0 1 0 1])
   (repeat 6 [0 0 1 1])
   (repeat 6 [1 1 0 1])
   (repeat 6 [1 0 1 1])
   (repeat 6 [0 1 1 1])])


(defn example-data []
  {pos-attribute   (mapcat identity (geom/tessellate (base-cuboid)))
   color-attribute (mapcat identity face-colors)})



(defn example-driver []
  (let [c (.getContext (.getElementById js/document "gl-canvas") "webgl")]
    (.enable c (.-DEPTH_TEST c))
    (.clear c (.-DEPTH_BUFFER_BIT c))
    (driver/basic-driver c)))



(defn main []
  (let [driver (example-driver)
        program (example-program)
        data (example-data)]
    (gd/draw-arrays
      driver
      (gd/bind driver program data)
      {})))




