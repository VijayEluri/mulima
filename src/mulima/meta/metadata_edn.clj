(ns mulima.meta.metadata-edn
  (:require [mulima.meta.generic :as meta]
            [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (java.nio.file Files OpenOption)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; High-level parsing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmethod meta/parse* "edn" [path]
  (with-open [stream (Files/newInputStream path (into-array OpenOption []))]
    (edn/read stream)))
