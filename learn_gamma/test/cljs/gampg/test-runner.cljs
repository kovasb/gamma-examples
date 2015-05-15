(ns learn-gamma.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [learn-gamma.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'learn-gamma.core-test))
    0
    1))
