(ns mulima.meta.album-xml
  (:require [mulima.meta.generic :refer [parse* denormalize]]
            [clojure.java.io :as io]
            [clojure.data.xml :as xml]
            [clojure.edn :as edn])
  (:import (java.nio.file Files OpenOption)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Parsing tag values
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmulti parse-value
  "Parses a tags value, dispatching on the tag name. This is meant to support
  parsing different tags to different types."
  (fn [name _] name))

(defmethod parse-value :discNumber [_ value] (edn/read-string value))
(defmethod parse-value :trackNumber [_ value] (edn/read-string value))
(defmethod parse-value :default [_ value] value)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Parsing XML elements
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmulti parse-xml-element
  "Parses a single xml element. This method dispatches on the element tag name.
  Implementers of this are expected to parse their child elements as well."
  :tag)

(defn- filter-tag-parse
  "Returns a transducer that parses only the elements that have a tag matching
  the one passed to this function."
  [tag]
  (comp (filter #(= tag (:tag %)))
        (map parse-xml-element)))

(defn- parse-parent-xml-element
  "Parses an element that is a parent of other tags. Must be passed the element
  that is the parent and the keyword to filter child tags on."
  [element child-tag]
  (let [content (:content element)
        tags (into {} (filter-tag-parse :tag) content)
        children (into [] (filter-tag-parse child-tag) content)]
    {:mulima.meta/tags tags :mulima.meta/children children}))

(defmethod parse-xml-element :tag [element]
  (let [{:keys [name value]} (:attrs element)
        name (keyword name)
        value (parse-value name value)]
    [name value]))

(defmethod parse-xml-element :startPoint [element]
  (let [time (-> element :attrs :time)]
    [nil time nil]))

(defmethod parse-xml-element :track [element]
  (let [content (:content element)
        tags (into {} (filter-tag-parse :tag) content)
        cues (let [xf (filter-tag-parse :startPoint)]
               (first (eduction xf content)))]
    {:mulima.meta/tags tags :mulima.meta/cues cues}))

(defmethod parse-xml-element :disc [element]
  (parse-parent-xml-element element :track))

(defmethod parse-xml-element :album [element]
  (parse-parent-xml-element element :disc))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Mapping start points of
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- start-point
  "Gets the start point of a track as a three element vector. The first element
  is disc number, second is track number, and third is the cuepoint indicating
  the start of the track."
  [track]
  [(get-in track [:mulima.meta/tags :discNumber])
   (get-in track [:mulima.meta/tags :trackNumber])
   (get-in track [:mulima.meta/cues 1])])

(defn- into-nested-map
  "Converts a collection of paths into a nested map, utilizing the given
  transducing function to transform the paths as needed. A path is a vector
  where the last element is the value and the previous values are the keys that
  should be used, in order, within the map."
  [xf coll]
  (let [rf (fn [m p]
            (let [ks (butlast p)
                  v (last p)]
              (assoc-in m ks v)))]
    (transduce xf (completing rf) {} coll)))

(defn- start-points
  "Gets the start points of all tracks in a map where the first key is the
  disc number, second is the track number, and the value is the cuepoint where
  the track starts."
  [data]
  (into-nested-map (map start-point) data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; High-level parsing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmethod parse* "album.xml" [path]
  (let [stream (Files/newInputStream path (into-array OpenOption []))
        contents (-> stream slurp xml/parse-str)
        data (-> contents parse-xml-element denormalize)
        points (start-points data)
        end-point (fn [track]
                    (let [dnum (get-in track [:mulima.meta/tags :discNumber])
                          tnum (get-in track [:mulima.meta/tags :trackNumber])
                          epoint (get-in points [dnum (inc tnum)])]
                      (assoc-in track [:mulima.meta/cues 2] epoint)))]
    (into [] (map end-point) data)))