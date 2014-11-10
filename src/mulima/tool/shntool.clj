(ns mulima.tool.shntool
  (:require [mulima.tool :as tool]
            [clojure.string :as string]))

(defrecord ShntoolOpts
  [path
   overwrite
   pregaps])

(defn- cue-start
  [opts cues]
  (or (and (:pregaps opts) (nth cues 0))
      (nth cues 1)))

(defn cue-points
  [opts cues]
  (->> cues
       (mapcat #((juxt (partial cue-start opts) last) %))
       (into (sorted-set))))

(defn- cue-input
  [cues]
  (map #(string/replace % #"(?<=:\d\d):" ".") cues))

(extend-type ShntoolOpts
  tool/Splitter
  (split! [opts tracks source dest-dir]
    nil))
