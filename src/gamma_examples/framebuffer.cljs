(ns gamma-examples.framebuffer
  (:require [gamma.api :as g]
            [gamma.program :as p]
            [gamma-driver.api :as gd]
            [gamma-driver.drivers.basic :as driver]
            [goog.dom :as gdom]
            [goog.webgl :as ggl]
            [gamma-examples.triangle :as triangle-ex]
            [gamma-examples.texture :as texture-ex]
            ))


(defn example-driver []
  (driver/basic-driver
    (.getContext (.getElementById js/document "gl-canvas") "webgl")))



(defn example-frame-buffer [driver w h]
  (gd/frame-buffer
    driver
    {:color (gd/texture
              driver
              {:width w
               :height h
               :texture-id 1
               :filter {:min :nearest}})
     :depth (gd/render-buffer
              driver
              {:width  w
               :height h
               :format :depth-component16})}))



(defn main
  []
  (let
    [w 512 h 512
     d (example-driver)
     program1 (gd/program d (triangle-ex/example-program))
     program2 (gd/program d (texture-ex/example-program))
     fb (example-frame-buffer d w h)

     data1 (triangle-ex/example-data)
     data2 (assoc
             (texture-ex/example-data nil)
             texture-ex/u-sampler
             (:color fb))]

    (gd/bind d program1 data1 )

    (.viewport (:gl d) 0 0 512 512)

    (gd/draw-arrays
      d
      program1
      {}
      fb)

    (gd/bind d program2 data2)

    (.viewport (:gl d) 0 0 512 512)

    (.bindTexture (:gl d) ggl/TEXTURE_2D (:texture (:color fb)))

    (gd/draw-arrays
      d
      program2
      {})))