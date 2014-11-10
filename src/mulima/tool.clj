(ns mulima.tool
  (:require [me.raynes.conch :as sh]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :refer [map-invert]]))

(defprotocol Splitter
  (split! [opts image dest-dir]))

(defprotocol Joiner
  (join! [opts files dest-image]))

(defprotocol Tagger
  (write-tags! [opts file tags])
  (read-tags! [opts file]))

(defprotocol Codec
  (encode! [opts source dest])
  (decode! [opts source dest]))

(defn cmd!
  [path & args]
  (let [out (java.io.StringWriter.)
        err (java.io.StringWriter.)]
    (try
      (sh/run-command path args {:out out :err err})
      (catch Exception e
        (do (println out)
            (println err)
            (throw e))))
    (str out)))

(defn tag-bimap
  [path]
  (with-open [rdr (-> (io/reader path)
                      (java.io.PushbackReader.))]
    (let [tags (edn/read rdr)
          itags (map-invert tags)]
      (merge tags itags))))
