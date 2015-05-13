(ns gamma-examples.mvp
  (:require
    [gamma-driver.drivers.basic :as driver]
    [gamma-driver.protocols :as dp]
    [gamma.program :as p]
    [gamma.api :as g]
    [gamma.webgl :as webgl]
    [thi.ng.geom.core :as geom]
    [thi.ng.geom.core.matrix :as mat :refer [M44]]
    [thi.ng.geom.webgl.arrays :as arrays]
    [thi.ng.geom.basicmesh :as bm]
    [thi.ng.geom.cuboid :as cuboid]
    [thi.ng.geom.webgl.core :as tgl]))


(defn ->radians [degrees]
  (/ (* degrees Math/PI) 180))


(def pos-attribute (g/attribute "a_Pos" :vec2))

(def pmatrix (g/uniform "u_PerspectiveMatrix" :mat4))

(def tmatrix (g/uniform "u_TriangleMatrix" :mat4))


(defn example-program []
  (p/program
    {:vertex-shader {(g/gl-position) (g/* pmatrix
                                          (g/* tmatrix
                                               (g/vec4 pos-attribute 0 1)))}
     :fragment-shader {(g/gl-frag-color) (g/vec4 1 0 0 1)}}
    ))


(defn get-position-matrix [v]
  (apply geom/translate M44 v))

(defn get-perspective-matrix [width height]
  (mat/perspective 45 (/ width height) 0.1 100.0))


(defn example-data []
  (let [triangle-matrix (geom/rotate-around-axis
                          (get-position-matrix [-1.5 0.0 -7.0])
                          [0 1 0]
                          (->radians 45))
        perspective-matrix (get-perspective-matrix 500 500)]
    {pos-attribute [[0 0.5] [-0.5 -0.5] [0.5 -0.5]]
     tmatrix       triangle-matrix
     pmatrix       perspective-matrix}))

(defn example-driver []
  (driver/basic-driver
    (.getContext (.getElementById js/document "gl-canvas") "webgl")))


(defn main []
  (let [d (example-driver)
        p (dp/program d (example-program))]
    (driver/draw-program d p (example-data))))