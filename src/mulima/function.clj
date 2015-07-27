(ns mulima.function
  (:import (java.util.function BiFunction BinaryOperator Supplier Consumer Function)))

(defn lambda [f]
  (reify
    Supplier
    (get [_] (f))
    Consumer
    (accept [_ x] (f x))
    Function
    (apply [_ x] (f x))
    BiFunction
    (apply [_ x y] (f x y))))

(defn ^BinaryOperator binary-operator [f]
  (reify BinaryOperator
    (apply [_ x y] (f x y))))

(defn ^BiFunction bi-function [f]
  (reify BiFunction
    (apply [_ x y] (f x y))))
