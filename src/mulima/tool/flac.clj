(ns mulima.tool.flac
  (:require [mulima.tool :refer :all]
            [me.raynes.conch :as sh]))

(defrecord FlacOpts
  [path
   compression-level])

(extend-type FlacOpts
  Codec
  (encode [opts source dest]
    nil)
  (decode [opts source dest]
    nil))

(defrecord MetaflacOpts
  [path])

(def ^:private tag-pattern #"(?m)^\s*comment\[[0-9]+\]:\s+(\w+)=(.*)\s*$")

(extend-type MetaflacOpts
  Tagger
  (write-tags [opts file]
    nil)
  (read-tags [opts file]
    (sh/let-programs [cmd (get opts :path "metaflac")]
      (let [writer (java.io.StringWriter.)]
        (cmd "--list" "--block-type=VORBIS_COMMENT" file {:out writer})
        (->> (str writer)
             (re-seq tag-pattern)
             (map (fn [[m k v]] [k v]))
             (into {}))))))
