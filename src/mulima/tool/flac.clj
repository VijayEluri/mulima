(ns mulima.tool.flac
  (:require [mulima.tool :as tool]))

(defrecord FlacOpts
  [path
   compression-level])

(extend-type FlacOpts
  tool/Codec
  (encode! [opts source dest]
    nil)
  (decode! [opts source dest]
    nil))

(defrecord MetaflacOpts
  [path])

(def ^:private tag-pattern #"(?m)^\s*comment\[[0-9]+\]:\s+(\w+)=(.*)\s*$")

(extend-type MetaflacOpts
  tool/Tagger
  (write-tags! [opts file tags]
    nil)
  (read-tags! [opts file]
    (->> (tool/cmd! (:path opts) "--list" "--block-type=VORBIS_COMMENT" file)
         (re-seq tag-pattern)
         (map (fn [[m k v]] [k v]))
         (into {}))))
