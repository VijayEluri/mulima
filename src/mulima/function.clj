(ns mulima.function
  (:import (java.util.function BiFunction BinaryOperator)))

(defn ^BinaryOperator binary-operator [f]
  (reify BinaryOperator
    (apply [_ x y] (f x y))))

(defn ^BiFunction bi-function [f]
  (reify BiFunction
    (apply [_ x y] (f x y))))
