(ns mulima.stream-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mulima.stream])
  (:import (java.util.stream IntStream)))

(deftest reduce-preserves-order
  (is (= [1 2 3 4] (->> (IntStream/range 1 5)
                          (into [])))))
