(ns gamma-examples.cuboid2
  (:require
    [gamma-driver.drivers.basic :as driver]
    [gamma-driver.protocols :as dp]
    [gamma.program :as p]
    [gamma.api :as g]
    [thi.ng.geom.core :as geom]
    [thi.ng.geom.core.matrix :as mat :refer [M44]]
    [thi.ng.geom.webgl.arrays :as arrays]
    [thi.ng.geom.basicmesh :as bm]
    [thi.ng.geom.cuboid :as cuboid]
    [thi.ng.geom.webgl.core :as tgl]
    [thi.ng.geom.core.utils :as gu]))

(def a-position (g/attribute "a_Position" :vec3))

(def a-color (g/attribute "a_Color" :vec4))

(def a-normal (g/attribute "a_Normal" :vec3))

;(def u-mvp-matrix (g/uniform "u_MvpMatrix" :mat4))

(def u-light-color (g/uniform "u_LightColor" :vec3))

(def u-light-direction (g/uniform "u_LightDirection" :vec3))

(def v-color (g/varying "v_Color" :vec4 :mediump))

(defn light-fragment [vertex-normal vertex-color light-direction light-color]
  (let [normal (g/normalize vertex-normal)
        nDotL (g/max (g/dot light-direction normal) 0.001)
        diffuse (g/* light-color
                     (g/* (g/swizzle vertex-color :rgb)
                          nDotL))]
    (g/vec4 diffuse (g/swizzle vertex-color :a))))


(defn example-program []
  (p/program
    {:vertex-shader
     {(g/gl-position) (g/vec4 a-position 1)
      v-color         (light-fragment a-normal a-color u-light-direction u-light-color)}
     :fragment-shader {(g/gl-frag-color) v-color}}))


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
  [
   (repeat 6 [1 0 0 1])
   (repeat 6 [1 0 0 1])

   (repeat 6 [1 0 0 1])
   (repeat 6 [1 0 0 1])
   (repeat 6 [1 0 0 1])
   (repeat 6 [1 0 0 1])])

(def triangles (geom/tessellate (base-cuboid)))

(def normals
  (mapcat
    #(repeat 3 (gu/ortho-normal %))
    triangles))

(def vertex-positions
  (mapcat identity triangles))

(defn example-data []
  (driver/normalize-data
    {a-position vertex-positions
     a-color face-colors
     a-normal normals
     u-light-color [1 1 1]
     u-light-direction [-0.5 -1 -1]}))


(defn example-driver []
  (let [c (.getContext (.getElementById js/document "gl-canvas") "webgl")]
    (.enable c (.-DEPTH_TEST c))
    (.clear c (.-DEPTH_BUFFER_BIT c))
    (driver/basic-driver c)))


(defn main []
  (let [d (example-driver)
        p (dp/program d (example-program))]
    (driver/draw-program d p (example-data))))








