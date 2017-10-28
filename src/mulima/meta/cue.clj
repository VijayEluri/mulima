(ns mulima.meta.cue
  (:require [mulima.meta.generic :refer [emit* parse* denormalize normalize]]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.edn :as edn]
            [clojure.data.xml :as xml]
            [clojure.string :refer [starts-with?]]
            [ike.cljj.file :as file]
            [ike.cljj.stream :as stream])
  (:import (java.nio.file Files OpenOption)))

(def generic->cue (edn/read-string (slurp (io/resource "cue-tags.edn"))))
(def cue->generic (set/map-invert generic->cue))

(def num-regex #".*\((\d+)\)\.cue")
(def line-regex #"^\s*((?:REM )?[A-Z]+) (?:0?(\d+) (.+)|\"(.*)\"|'(.*)'|(.*))")

(defn groups [regex value]
  (let [matcher (re-matcher regex value)]
    (if (re-find matcher)
      (re-groups matcher))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Parsing tag values
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmulti parse-value
  "Parses a tags value, dispatching on the tag name. This is meant to support
  parsing different tags to different types."
  second)

(defmethod parse-value "TRACK" [line]
  [:mulima.meta/tags {:track-number (edn/read-string (get line 2))}])
(defmethod parse-value "INDEX" [line]
  [:mulima.meta/cues (assoc [nil nil nil] (edn/read-string (get line 2)) (get line 3))])
(defmethod parse-value :default [line]
  (let [name (if (starts-with? (get line 0) "TITLE")
               :album
               (cue->generic (get line 1)))
        value (some #(get line %) [4 5 6])]
    (if name
      [:mulima.meta/tags {name value}]
      nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Parsing CUE elements
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn merge-line [data [key value]]
  (case key
    :mulima.meta/tags (update data key #(merge % value))
    :mulima.meta/cues (assoc data key value)))

(defn track-num [scope]
  (fn [[key value]]
    (if (contains? value :track-number)
      (swap! scope inc)
      @scope)))

(defn parse-cue [lines]
  (let [scope (atom 0)
        partitioned (partition-by (track-num scope) (keep identity lines))
        root (reduce merge-line {} (first partitioned))
        tracks (rest partitioned)]
    (assoc root :mulima.meta/children (into [] (map #(reduce merge-line {} %)) tracks))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Mapping start points of
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- start-point
  "Gets the start point of a track as a three element vector. The first element
  is disc number, second is track number, and third is the cuepoint indicating
  the start of the track."
  [track]
  [(get-in track [:mulima.meta/tags :disc-number])
   (get-in track [:mulima.meta/tags :track-number])
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
;; High-level functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmethod parse* "cue" [path]
  (let [disc-num (or (->> path .getFileName str (groups num-regex) second) 1)
        contents (-> path file/lines stream/stream-seq)
        lines (map #(groups line-regex %) contents)
        data (->> lines (map parse-value) parse-cue denormalize)
        points (start-points data)
        end-point (fn [track]
                    (let [dnum (get-in track [:mulima.meta/tags :disc-number])
                          tnum (get-in track [:mulima.meta/tags :track-number])
                          epoint (get-in points [dnum (inc tnum)])]
                      (assoc-in track [:mulima.meta/cues 2] epoint)))]
    (into [] (map end-point) data)))
