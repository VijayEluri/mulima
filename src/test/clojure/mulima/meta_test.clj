(ns mulima.meta-test
  (:require [mulima.meta :refer :all]
            [clojure.test :refer [deftest is]]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]))

(deftest valid-cuepoints
  (is (cuepoint? "00:00:00"))
  (is (cuepoint? "9999:59:74"))
  (is (cuepoint? "00:00.000"))
  (is (cuepoint? "9999:59.999")))
