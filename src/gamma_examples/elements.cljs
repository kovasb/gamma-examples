(ns gamma-examples.elements
  (:require
    [gamma.program :as p]
    [gamma.api :as g]
    [gamma-driver.api :as gd]
    [gamma-driver.drivers.basic :as driver]))



(def a-position (g/attribute "a_Position" :vec2))

(defn example-program []
  (p/program
    {:vertex-shader {(g/gl-position) (g/vec4 a-position 0 1)}
     :fragment-shader {(g/gl-frag-color) (g/vec4 1 0 0 1)}}))


(defn example-data []
  {a-position  [[-0.5 0.5]
                [-0.5 -0.5]
                [0.5 0.5]
                [0.5 -0.5]]
   {:tag :element-index} [0 1 2 1 2 3]})


(defn example-driver []
  (let [c (.getContext (.getElementById js/document "gl-canvas") "webgl")]
    (driver/basic-driver c)))

(defn main []
  (let [driver (example-driver)
        program (example-program)
        data (example-data)]
    (gd/draw-elements
      driver
      (gd/bind driver program data)
      {:count 6})))


