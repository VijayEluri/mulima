(ns mulima.tool.shntool
  (:require [mulima.tool :as tool]))

(defrecord ShntoolOpts
  [path])

(extend-type ShntoolOpts
  tool/Splitter
  (split! [opts image dest-dir]
    nil))
