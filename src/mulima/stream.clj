(ns mulima.stream
  "Adds reduce (and thus trandsuce, into, etc) support to
  java.util.stream.BaseStream."
  (:require [clojure.core.protocols :refer [CollReduce coll-reduce]]
            [mulima.function :as fun])
  (:import (java.util.stream BaseStream StreamSupport)
           (java.util Spliterator)))

(defn- ^Spliterator early-spliterator
  "Wrap a Spliterator such that it will short-curcuit
  when the promise 'done' is realized."
  [^Spliterator spliterator done]
  (reify Spliterator
    (characteristics [_]
      (-> spliterator
          .characteristics
          (bit-and-not Spliterator/SIZED)))
    (estimateSize [_]
      (if-not (realized? done)
        (.estimateSize spliterator)
        0))
    (tryAdvance [_ action]
      (and (not (realized? done))
           (.tryAdvance spliterator action)))
    (trySplit [_] nil)))

(defn- ^BaseStream early-stream
  "Wrap a Stream such that it will short-curcuit
  when the promise 'done' is realized."
  [^BaseStream stream done]
  (-> stream
      .spliterator
      (early-spliterator done)
      (StreamSupport/stream false)))

(defn- accumulator
  "Creates an accumulator function for use
  by Stream.reduce() that handles reduced
  values."
  [f done]
  (fn [acc val]
    (let [ret (f acc val)]
      (if (reduced? ret)
        (do
          (deliver done true)
          @ret)
        ret))))

(extend-protocol CollReduce
  BaseStream
  (coll-reduce
    ([stream f] (coll-reduce stream f (f)))
    ([stream f init]
      (let [done (promise)]
        (with-open [estream (early-stream stream done)]
          (.reduce estream
                   init
                   (-> f (accumulator done) fun/bi-function)
                   (fun/binary-operator #(throw (Exception. "Combine should not be called.")))))))))
