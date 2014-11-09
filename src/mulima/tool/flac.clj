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
  [path
   tag-props])

(def ^:private tag-pattern #"(?m)^\s*comment\[[0-9]+\]:\s+(\w+)=(.*)\s*$")

(defn- tag-mapper
  [tags]
  (fn [[tag value]]
    (let [vtag (get tags tag)]
      (str "--set-tag=" vtag "=" value))))

(extend-type MetaflacOpts
  tool/Tagger
  (write-tags! [opts file tags]
    (let [all-tags (tool/tag-bimap (:tag-props opts))
          tag->arg (tag-mapper all-tags)
          sargs (map tag->arg tags)]
      (apply tool/cmd! (:path opts) "--remove-all-tags" (conj sargs file))))
  (read-tags! [opts file]
    (->> (tool/cmd! (:path opts) "--list" "--block-type=VORBIS_COMMENT" file)
         (re-seq tag-pattern)
         (map (fn [[m k v]]
                [(get (tool/tag-bimap (:tag-props opts)) k) v]))
         (filter (fn [[k v]] k))
         (into {}))))
