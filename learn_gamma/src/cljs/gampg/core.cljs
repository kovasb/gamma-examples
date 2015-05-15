(ns ^:figwheel-always learn-gamma.core
  (:require [clojure.string :as s]
            [gamma.api :as g]
            [gamma.program :as p]
            [gamma.tools :as gt]
            [gamma-driver.drivers.basic :as driver]
            [gamma-driver.protocols :as dp]
            [learn-gamma.learn-gamma.lesson-01 :as lg01]
            [learn-gamma.learn-gamma.lesson-02 :as lg02]
            [learn-gamma.learn-gamma.lesson-03 :as lg03]
            [learn-gamma.learn-gamma.lesson-04 :as lg04]
            [learn-gamma.learn-gamma.lesson-05 :as lg05]
            [learn-gamma.learn-gamma.lesson-06 :as lg06]
            [learn-gamma.learn-gamma.lesson-07 :as lg07]
            [learn-gamma.learn-gamma.lesson-08 :as lg08]
            [learn-gamma.learn-gamma.lesson-09 :as lg09]
            [learn-gamma.learn-gamma.lesson-10 :as lg10]
            [goog.webgl :as ggl]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            ;; TEMPORARY
            [thi.ng.geom.core :as geom]
            [thi.ng.geom.core.matrix :as mat :refer [M44]]
            [thi.ng.geom.webgl.arrays :as arrays]
            [thi.ng.geom.basicmesh :as bm]
            [thi.ng.geom.cuboid :as cuboid]
            [thi.ng.geom.webgl.core :as tgl]
            [thi.ng.geom.core.utils :as gu]))

(enable-console-print!)

;; 07-09 are broken
(def title   lg07/title)
(def prog    lg07/program-source)
(def gl-main lg07/main)

(defonce app-state (atom {:live {}}))

(defn canvas [data owner opts]
  (reify
    om/IDidMount
    (did-mount [_]
      (let [cb   (:cb opts)
            node (om/get-node owner)
            gl   (.getContext node "webgl")]
        (when gl
          (cb gl node))))
    om/IRender
    (render [_]
      (dom/canvas #js{:id "gl-canvas"
                      :style #js{:width "100%"
                                 :border (str "2px solid " (rand-nth ["black" "blue" "green" "pink"]))}}))))

(defn main []
  (om/root
   (fn [app owner]
     (reify
       om/IRender
       (render [_]
         (dom/div nil
                  (dom/h2 #js{:onClick (fn [event] (om/transact! app :reverse? not))}
                          title)
                  (dom/small nil
                             (dom/pre #js{:style #js{:float "left"
                                                     :borderRight "1px dotted black"
                                                     :width "45%"
                                                     :overflow "hidden"}}
                                      "Vertex Shader:\n--------------\n\n"
                                      (get-in prog [:vertex-shader :glsl])))
                  (dom/small nil
                             (dom/pre #js{:style #js{:width "50%"
                                                     :float "left"
                                                     
                                                     :marginLeft 4
                                                     :paddingLeft 4}}
                                      "Fragment Shader:\n----------------\n"
                                      (get-in prog [:fragment-shader :glsl])))
                  (om/build canvas (:text app) {:opts {:cb (fn [gl node]
                                                             (swap! app-state update-in [:live] assoc
                                                                    :gl gl
                                                                    :node node)
                                                             (gl-main gl node))}})))))
   app-state
   {:target (. js/document (getElementById "app"))}))
