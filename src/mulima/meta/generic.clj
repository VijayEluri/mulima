(ns mulima.meta.generic
  (:require [clojure.java.io :as io]
            [ike.cljj.file :as file]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generic Parsing/Emitting
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- file-name
  [path-str]
  (-> path-str file/path .getFileName str))

(defmulti parse* file-name)

(defmulti emit* (fn [path-str _] (file-name path-str)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn normalize
  [data]
  nil)

(defn- denormalize*
  [data]
  (if (contains? data :mulima.meta/children)
    (let [children (:mulima.meta/children data)
          tags (:mulima.meta/tags data)
          merge-tags (fn [meta] (update meta :mulima.meta/tags #(merge tags %)))
          xf (comp (map merge-tags)
                   (mapcat denormalize*))]
      (eduction xf children))
    [data]))

(defn denormalize
  [data]
  (into [] (denormalize* data)))
