(ns mulima.tool
  (:require [me.raynes.conch :as sh]))

(defprotocol Splitter
  (split! [opts image dest-dir]))

(defprotocol Joiner
  (join! [opts files dest-image]))

(defprotocol Tagger
  (write-tags! [opts file])
  (read-tags! [opts file]))

(defprotocol Codec
  (encode! [opts source dest])
  (decode! [opts source dest]))

(defn cmd!
  [path & args]
  (let [writer (java.io.StringWriter.)]
    (sh/run-command path args {:out writer})
    (str writer)))
