(ns mulima.tool.shntool
  (:require [mulima.tool :as tool]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defrecord ShntoolOpts
  [path
   overwrite
   pregaps])

(defn- cue-start
  [opts cues]
  (nth cues (if (:pregaps opts) 0 1)))

(defn cue-points
  [opts tracks]
  (->> tracks
       (map :cues)
       (mapcat (juxt (partial cue-start opts) last))
       (remove #{"00:00:00"})
       (into (sorted-set))))

(defn- cue-input
  [cues]
  (->> cues
       (map #(string/replace % #"(?<=:\d\d):" "."))
       (string/join "\n")))

(defn- tracks-by-cue-end
  [tracks]
  (group-by (comp last :cues) tracks))

(defn- files-to-tracks
  [files tracks]
  nil)

(defn file-to-index
  [file]
  (->> (io/file file)
       (.getName)
       (re-matches #"split-track(\d+)\.wav")
       (last)
       (read-string)))

(defn dir-to-indices
  [dest-dir]
  (->> (io/file dest-dir)
       (.listFiles)
       (group-by file-to-index)))

(extend-type ShntoolOpts
  tool/Splitter
  (split! [opts tracks source dest-dir]
    (let [cues (cue-points opts tracks)
          in (cue-input cues)
          oarg (if (:overwrite opts) "always" "never")]
      (io/make-parents dest-dir)
      (tool/cmd! (:path opts) in ["split" "-O" oarg "-d" dest-dir source])
      nil)))
