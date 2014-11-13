(ns mulima.tool
  (:require [me.raynes.conch :as sh]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :refer [map-invert]]))

(defprotocol Splitter
  (split! [opts tracks source dest-dir]))

(defprotocol Joiner
  (join! [opts files dest]))

(defprotocol Tagger
  (write-tags! [opts file tags])
  (read-tags! [opts file]))

(defprotocol Codec
  (encode! [opts source dest])
  (decode! [opts source dest]))

(defn cmd!
  ([path args]
   (apply path "" args))
  ([path in args]
   (let [out (java.io.StringWriter.)
         err (java.io.StringWriter.)]
     (try
       (sh/run-command path args {:in in :out out :err err})
       (catch Exception e
         (do (println (str out))
             (println (str err))
             (throw e))))
     (str out))))

(defn tag-bimap
  [path]
  (with-open [rdr (-> (io/reader path)
                      (java.io.PushbackReader.))]
    (let [tags (edn/read rdr)
          itags (map-invert tags)]
      (merge tags itags))))
