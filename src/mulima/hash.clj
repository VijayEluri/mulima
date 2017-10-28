(ns mulima.hash
  (:require [ike.cljj.file :as file]
            [clojure.java.io :as io])
  (:import [org.apache.commons.codec.digest DigestUtils]))

(defn sha1 [path]
  (with-open [is (io/input-stream path)]
    (DigestUtils/sha1Hex is)))
