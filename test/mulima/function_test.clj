(ns mulima.function-test
  (:require [clojure.test :refer :all]
            [mulima.function :refer :all])
  (:import (java.util.stream Stream)))

(deftest ifn-used-as-supplier
  (is (= 42 (-> (Stream/generate (lambda (fn [] 42)))
                .findFirst
                .get))))

(deftest ifn-used-as-function
  nil)


