(ns mulima.tag
  (:require [mulima.tag.generic :as generic]))

(defn parse [path-str]
  (generic/parse* path-str))

(defn emit [path-str data]
  (generic/emit* path-str data))
