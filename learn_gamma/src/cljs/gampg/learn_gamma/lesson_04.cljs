(ns ^:figwheel-load learn-gamma.learn-gamma.lesson-04
    (:require [clojure.string :as s]
            [gamma.api :as g]
            [gamma.program :as p]
            [gamma.tools :as gt]
            [gamma-driver.drivers.basic :as driver]
            [gamma-driver.protocols :as dp]
            [goog.webgl :as ggl]
            [thi.ng.geom.core :as geom]
            [thi.ng.geom.core.matrix :as mat :refer [M44]]
            [thi.ng.geom.webgl.arrays :as arrays]))

(def title
  "Some Real 3D Objects")

(def u-p-matrix
  (g/uniform "uPMatrix" :mat4))

(def u-mv-matrix
  (g/uniform "uMVMatrix" :mat4))

(def a-position
  (g/attribute "aVertexPosition" :vec3))

(def a-color
  (g/attribute "aVertexColor" :vec4))

(def v-color
  (g/varying "vColor" :vec4 :mediump))

(def program-source
  (p/program
   {:vertex-shader   {(g/gl-position) (-> u-p-matrix
                                          (g/* u-mv-matrix)
                                          (g/* (g/vec4 a-position 1)))
                      v-color         a-color}
    :fragment-shader {(g/gl-frag-color) v-color}}))

(defn get-perspective-matrix
  "Be sure to 
   1. pass the WIDTH and HEIGHT of the canvas *node*, not
      the GL context
   2. (set! (.-width/height canvas-node)
      width/height), respectively, or you may see no results, or strange
      results"
  [width height]
  (mat/perspective 45 (/ width height) 0.1 100))

(defn get-data [p mv vertices vertex-colors]
  {u-p-matrix  p
   u-mv-matrix mv
   a-position  vertices
   a-color     vertex-colors})

(defn make-driver [gl]
  (driver/basic-driver gl))

(defn reset-gl-canvas! [canvas-node]
  (let [gl     (.getContext canvas-node "webgl")
        width  (.-clientWidth canvas-node)
        height (.-clientHeight canvas-node)]
    ;; Set the width/height (in terms of GL-resolution) to actual
    ;; canvas-element width/height (or else you'll see blurry results)
    (set! (.-width canvas-node) width)
    (set! (.-height canvas-node) height)
    ;; Setup GL Canvas
    (.viewport gl 0 0 width height)))

;; js/window.requestAnimationFrame doesn't take arguments, so we have
;; to store the state elsewhere - in this atom, for example.
(defn app-state [width height]
  {:last-rendered 0
   :scene         {:pyramid-vertices [ ;; Front face
                                      [0.0,  1.0,  0.0,]
                                      [-1.0, -1.0,  1.0,]
                                      [1.0, -1.0,  1.0,]
                                      ;; Right face
                                      [0.0,  1.0,  0.0,]
                                      [1.0, -1.0,  1.0,]
                                      [1.0, -1.0, -1.0,]
                                      ;; Back face
                                      [0.0,  1.0,  0.0,]
                                      [1.0, -1.0, -1.0,]
                                      [-1.0, -1.0, -1.0,]
                                      ;; Left face
                                      [0.0,  1.0,  0.0,]
                                      [-1.0, -1.0, -1.0,]
                                      [-1.0, -1.0,  1.0]]
                   :pyramid-colors   [ ;; Front face
                                      [1.0, 0.0, 0.0, 1.0,]
                                      [0.0, 1.0, 0.0, 1.0,]
                                      [0.0, 0.0, 1.0, 1.0,]
                                      ;; Right face
                                      [1.0, 0.0, 0.0, 1.0,]
                                      [0.0, 0.0, 1.0, 1.0,]
                                      [0.0, 1.0, 0.0, 1.0,]
                                      ;; Back face
                                      [1.0, 0.0, 0.0, 1.0,]
                                      [0.0, 1.0, 0.0, 1.0,]
                                      [0.0, 0.0, 1.0, 1.0,]
                                      ;; Left face
                                      [1.0, 0.0, 0.0, 1.0,]
                                      [0.0, 0.0, 1.0, 1.0,]
                                      [0.0, 1.0, 0.0, 1.0]]
                   :pyramid-rotation 0
                   :cube-vertices    [ ;; Front face
                                      [-1.0 -1.0  1.0]
                                      [1.0 -1.0  1.0]
                                      [1.0  1.0  1.0]
                                      [-1.0  1.0  1.0]
                                      
                                      ;; Back face
                                      [-1.0 -1.0 -1.0]
                                      [-1.0  1.0 -1.0]
                                      [1.0  1.0 -1.0]
                                      [1.0 -1.0 -1.0]
                                      
                                      ;; Top face
                                      [-1.0  1.0 -1.0]
                                      [-1.0  1.0  1.0]
                                      [1.0  1.0  1.0]
                                      [1.0  1.0 -1.0]
                                      
                                      ;; Bottom face
                                      [-1.0 -1.0 -1.0]
                                      [1.0 -1.0 -1.0]
                                      [1.0 -1.0  1.0]
                                      [-1.0 -1.0  1.0]
                                      
                                      ;; Right face
                                      [1.0 -1.0 -1.0]
                                      [1.0  1.0 -1.0]
                                      [1.0  1.0  1.0]
                                      [1.0 -1.0  1.0]
                                      
                                      ;; Left face
                                      [-1.0 -1.0 -1.0]
                                      [-1.0 -1.0  1.0]
                                      [-1.0  1.0  1.0]
                                      [-1.0  1.0 -1.0]]
                   :cube-colors      (vec (mapcat identity
                                                  [(repeat 4 [1.0, 0.0, 0.0, 1.0]), ;; Front face
                                                   (repeat 4 [1.0, 1.0, 0.0, 1.0]), ;; Back face
                                                   (repeat 4 [0.0, 1.0, 0.0, 1.0]), ;; Top face

                                                   (repeat 4 [1.0, 0.5, 0.5, 1.0]), ;; Bottom face
                                                   (repeat 4 [1.0, 0.0, 1.0, 1.0]), ;; Right face
                                                   (repeat 4 [0.0, 0.0, 1.0, 1.0]), ;; Left face
                                                   ]))
                   :cube-indices     [0  1  2     0  2  3  ;; Front face
                                      4  5  6     4  6  7  ;; Back face
                                      8  9  10    8 10 11  ;; Top face
                                      12 13 14   12 14 15  ;; Bottom face
                                      16 17 18   16 18 19  ;; Right face
                                      20 21 22   20 22 23;;
                                      ] ;; Left face
                   :cube-rotation    0
                   :mv               (mat/matrix44)
                   :p                (get-perspective-matrix width height)}})

(defn draw-fn [gl driver program]
  (fn [state]
    (.clear gl (bit-or (.-COLOR_BUFFER_BIT gl) (.-DEPTH_BUFFER_BIT gl)))
    (let [{:keys [p mv
                  pyramid-vertices pyramid-colors
                  pyramid-rotation
                  cube-vertices cube-colors
                  cube-rotation cube-indices
                  square-vertices square-colors
                  square-rotation]} (:scene state)]
      (let [mv (-> mv
                   (geom/translate [-1.5 0 -7])
                   (geom/rotate-y pyramid-rotation))]
        (driver/draw-arrays driver program (get-data p mv pyramid-vertices pyramid-colors) {:draw-mode :triangles}))
      (let [mv (-> mv
                   (geom/translate [3 0 -7])
                   (geom/rotate-x cube-rotation))]
        (driver/draw-elements driver program
                          (assoc (get-data p mv cube-vertices cube-colors)
                            {:tag :element-index} cube-indices)
                          {:draw-mode :triangles
                           :first 0
                           :count 36 ;;Hard-coded
                           })))))

(defn animate [draw-fn step-fn current-value]
  (js/requestAnimationFrame
   (fn [time]
     (let [next-value (step-fn time current-value)]
       (draw-fn next-value)
       (animate draw-fn step-fn next-value)))))

(defn tick
  "Takes the old world value and produces a new world value, suitable
  for rendering"
  [time state]
  ;; We get the elapsed time since the last render to compensate for
  ;; lag, etc.
  (let [time-now     (.getTime (js/Date.))
        elapsed      (- time-now (:last-rendered state))
        pyramid-diff (/ (* 50 elapsed) 100000)
        cube-diff    (/ (* 75 elapsed) 100000)]
    (-> state
        (update-in [:scene :pyramid-rotation] + pyramid-diff)
        (update-in [:scene :cube-rotation] + cube-diff)
        (assoc-in [:last-rendered] time-now))))

(defn main [gl node]
  
  (let [width     (.-clientWidth node)
        height    (.-clientHeight node)
        driver    (make-driver gl)
        program   (dp/program driver program-source)
        state (app-state width height)]
    (set! (.-debugRedrawScene js/window)
          (fn []
            (animate (draw-fn gl driver program) tick state)))
    (reset-gl-canvas! node)
    (.enable gl (.-DEPTH_TEST gl))
    (.clearColor gl 0 0 0 1)
    (.clear gl (bit-or (.-COLOR_BUFFER_BIT gl) (.-DEPTH_BUFFER_BIT gl)))
    (animate (draw-fn gl driver program) tick state)))
