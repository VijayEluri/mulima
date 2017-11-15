(ns mulima.tool.neroaac
  (:require [mulima.tool :as tool]
            [clojure.string :as string]))

(def ^:private tag-pattern #"([A-Za-z]+) = (.+)")

(defn- reverse-tags
  [opts tags]
  (let [tag-defs (tool/tag-bimap "id3v2-tags.edn")]
    (map (fn [[t v]] [(get tag-defs t) v]) tags)))

(defrecord NeroAacOpts
  [enc-path dec-path tag-path quality]
  tool/Codec
  (encode! [opts source dest]
    (tool/cmd! (:enc-path opts)
               ["-q" (:quality opts)
                "-if" source
                "-of" dest]))
  (decode! [opts source dest]
    (tool/cmd! (:dec-path opts)
               ["-if" source
                "-of" dest]))
  tool/Tagger
  (write-tags! [opts file tags]
    (let [reversed (reverse-tags opts tags)
          tag-args (map (fn [[t v]] (str "-meta-user:" t "=" (string/replace v #"\"" "\\\""))) reversed)
          args (conj tag-args file)]
      (tool/cmd! (:path opts) args)))
  (read-tags! [opts file]
    (->> (tool/cmd! (:path opts) [file "-list-meta"])
         (re-seq tag-pattern)
         (map (fn [[m k v]] [k v]))
         (reverse-tags opts)
         (filter (fn [[k v]] k))
         (into {}))))
