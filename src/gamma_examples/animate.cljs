(ns gamma-examples.animate
  (:require
    [gamma-driver.drivers.basic :as driver]
    [gamma-driver.protocols :as dp]
    [gamma.program :as p]
    [gamma.api :as g]
    [thi.ng.geom.core :as geom]
    [thi.ng.geom.core.matrix :as mat :refer [M44]]))


(def pos-attribute (g/attribute "a_Pos" :vec2))

(def tmatrix (g/uniform "u_TriangleMatrix" :mat4))

(def color-attribute (g/attribute "colorAttr" :vec4))

(def color-varying (g/varying "colorVarying" :vec4 :mediump))


(defn example-program []
  (p/program
    {:vertex-shader {(g/gl-position) (g/* tmatrix (g/vec4 pos-attribute 0 1))
                       color-varying   color-attribute}
     :fragment-shader {(g/gl-frag-color) color-varying}}))


(defn base-data []
  (driver/normalize-data
    {pos-attribute [[0 0.5] [-0.5 -0.5] [0.5 -0.5]]
     color-attribute [[1 0 0 1] [0 1 0 1] [0 0 1 1]]}))

(defn ->radians [degrees]
  (/ (* degrees Math/PI) 180))

(defn rotate [m deg]
  (geom/rotate-around-axis m [0 1 0] (->radians deg)))


(defn example-driver []
  (driver/basic-driver
    (.getContext (.getElementById js/document "gl-canvas") "webgl")))

(defn draw-fn [d p]
  (fn [triangle-mv-matrix]
    (driver/draw-program
      d
      p
      (driver/normalize-data {tmatrix triangle-mv-matrix}))))

(defn animate [drawfn stepfn current-value]
  (js/requestAnimationFrame
    (fn []
      (let [next-value (stepfn current-value)]
        (drawfn next-value)
        (animate drawfn stepfn next-value)))))


(defn main []
  (let [d (example-driver)
        p (dp/program d (example-program))
        base-data (base-data)]
    (driver/bind d p base-data)
    (animate
      (draw-fn d p)
      #(rotate % 1)
      M44)))






