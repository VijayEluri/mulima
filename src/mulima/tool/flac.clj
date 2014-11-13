(ns mulima.tool.flac
  (:require [mulima.tool :as tool]))

(defrecord FlacOpts
  [path
   compression-level])

(extend-type FlacOpts
  tool/Codec
  (encode! [opts source dest]
    (tool/cmd! (:path opts)
               [(str "-" (:compression-level opts))
                "-f" "-o" dest source]))
  (decode! [opts source dest]
    (tool/cmd! (:path opts)
               ["-d" "-f" "-o" dest source])))

(defrecord MetaflacOpts
  [path
   tag-props])

(def ^:private tag-pattern #"(?m)^\s*comment\[[0-9]+\]:\s+(\w+)=(.*)\s*$")

(defn- reverse-tags
  [opts tags]
  (let [tag-defs (tool/tag-bimap (:tag-props opts))]
    (map (fn [[t v]] [(get tag-defs t) v]) tags)))

(extend-type MetaflacOpts
  tool/Tagger
  (write-tags! [opts file tags]
    (let [reversed (reverse-tags opts tags)
          tag-args (map (fn [[t v]] (str "--set-tag=" t "=" v)) reversed)
          args (cons "--remove-all-tags" (conj tag-args file))]
      (tool/cmd! (:path opts) args)))
  (read-tags! [opts file]
    (->> (tool/cmd! (:path opts) ["--list" "--block-type=VORBIS_COMMENT" file])
         (re-seq tag-pattern)
         (map (fn [[m k v]] [k v]))
         (reverse-tags opts)
         (filter (fn [[k v]] k))
         (into {}))))
