(ns mulima.meta.generic
  (:require [clojure.java.io :as io]
            [clojure.spec :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generic Parsing/Emitting
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- file-name
  [path]
  (str (.getFileName path)))

(defmulti parse*
  "Parses the given Path as metadata. Dispatches on the file name."
  file-name)

(defmulti emit*
  "Emits the given data to the Path provided. Dispatches on the file name."
  (fn [path _] (file-name path)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn normalize
  "..."
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
  "Denormalizes a metadata with possible nesting (e.g. populated
  :mulima.meta/children). Returns a vector of metadata representing each leaf
  (i.e. track) with the tags of their previous parents included."
  [data]
  (into [] (denormalize* data)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Specs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(s/fdef normalize)
:args (s/cat :data (s/+ :mulima.meta/metadata))
:ret :mulima.meta/metadata

(s/fdef denormalize
  :args (s/cat :data :mulima.meta/metadata)
  :ret (s/+ :mulima.meta/metadata))
