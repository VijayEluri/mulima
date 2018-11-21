(ns mulima.job
  (:require [mulima.tool :as tool]))

(defmulti ^:private to-files :format)

(defmethod to-files :tracks [album]
  (->> (:metadata album)
       (map (juxt :source identity))
       (into {})))

(defmethod to-files :images [album]
  (->> (:metadata album)
       (group-by :source)
       (mapcat (fn [[s ts]] (tool/split! nil ts s nil)))
       (into {})))

(defn convert!
  [album dest-dir dest-format]
  nil)
