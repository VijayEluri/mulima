(ns mulima.job
  (:require [mulima.tool :as tool]))

(defmulti ^:private to-files (fn [album] (:format album)))

(defmethod to-files :tracks [album]
  (->> (:metadata album)
       (map (juxt :source identity))
       (into {})))

(defmethod to-files :images [album]
  (->> (:metadata album)
       (group-by :source)
       (mapcat (fn [[s ts]] (tool/cmd! nil ts s nil)))
       (into {})))

(defn convert!
  [album dest-dir dest-format]
  nil)
