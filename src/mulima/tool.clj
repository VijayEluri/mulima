(ns mulima.tool)

(defprotocol Splitter
  (split [this image dest-dir]))

(defprotocol Joiner
  (join [this files dest-image]))

(defprotocol Tagger
  (write-tags [this file])
  (read-tags [this file]))

(defprotocol Codec
  (encode [this source dest])
  (decode [this source dest]))
