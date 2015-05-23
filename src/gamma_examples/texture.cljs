(ns gamma-examples.texture
  (:require
    [gamma.program :as p]
    [gamma.api :as g]
    [gamma-driver.api :as gd]
    [gamma-driver.drivers.basic :as driver]))

;; WIP/BUSTED

(defn ->radians [degrees]
  (/ (* degrees Math/PI) 180))



(def a-position (g/attribute "a_Position" :vec2))

(def a-tex-coord (g/attribute "a_TexCoord" :vec2))

(def v-tex-coord (g/varying "v_TexCoord" :vec2 :mediump))

(def u-sampler (g/uniform "u_Sampler" :sampler2D))


(defn example-program []
  (p/program
    {:vertex-shader
     {(g/gl-position) (g/vec4 a-position 0 1)
      v-tex-coord     a-tex-coord}
     :fragment-shader {(g/gl-frag-color) (g/texture2D u-sampler v-tex-coord)}}))


(defn example-data [image]
  {a-position  [[-0.5 0.5] [-0.5 -0.5] [0.5 0.5]
                [-0.5 -0.5] [0.5 0.5] [0.5 -0.5]]
   a-tex-coord [[0 1] [0 0] [1 1]
                [0 0] [1 1] [1 0]]
   u-sampler   {:data     image
                :unpack   {:flip-y true}
                :filter   {:min :linear :mag :nearest}
                :id       0}
   })


(defn example-driver []
  (let [c (.getContext (.getElementById js/document "gl-canvas") "webgl")]
    (.enable c (.-DEPTH_TEST c))
    (.clear c (.-DEPTH_BUFFER_BIT c))
    (driver/basic-driver c)))



(defn main []
  (let [image (js/Image.)
        d (example-driver)
        p (example-program)]
    (aset image "onload"
          (fn [] (gd/draw-arrays
                   d
                   (gd/bind d p (example-data image))
                   {})))
    (aset image "src" "nehe.png")))
