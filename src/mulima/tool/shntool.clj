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

(defn- cue-points
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
  (->> tracks
       (map (fn [t] [((comp last :cues) t) t]))
       (into {})))

(defn- cues-to-files
  [cues dest-dir]
  (let [files (-> (io/file dest-dir)
                  (.listFiles)
                  (sort))]
    (zipmap cues files)))

(defn- tracks-to-files
  [tracks files]
  (->> (merge-with vector tracks files)
       (vals)
       (filter vector?)
       (into {})))

(extend-type ShntoolOpts
  tool/Splitter
  (split! [opts tracks source dest-dir]
    (let [cues (cue-points opts tracks)
          in (cue-input cues)
          oarg (if (:overwrite opts) "always" "never")]
      (io/make-parents dest-dir)
      (tool/cmd! (:path opts) in ["split" "-O" oarg "-d" dest-dir source])
      (let [fmap (cues-to-files cues dest-dir)
            tmap (tracks-by-cue-end tracks)]
        (tracks-to-files tmap fmap)))))
