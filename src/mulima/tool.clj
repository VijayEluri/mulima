(ns mulima.tool)

(defprotocol Splitter
  (split [opts image dest-dir]))

(defprotocol Joiner
  (join [opts files dest-image]))

(defprotocol Tagger
  (write-tags [opts file])
  (read-tags [opts file]))

(defprotocol Codec
  (encode [opts source dest])
  (decode [opts source dest]))
