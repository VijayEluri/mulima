(ns mulima.meta.metadata-edn
  (:require [mulima.meta.generic :as meta]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defmethod meta/parse* "metadata.edn" [path-str]
  (with-open [is (io/input-stream path-str)]
    (edn/read is)))
