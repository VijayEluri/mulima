(ns mulima.index
  (:require [mulima.tool :as tool]
            [mulima.tool.flac :as flac]
            [mulima.hash :as hash]
            [ike.cljj.file :as file]
            [ike.cljj.stream :as stream]
            [clojure.java.io :as io]
            [clojure.string :as str]))

;; {"<path>" {:hash '<file hash>'}}
;;            :tags {...}
(defonce data (atom {}))

(def metaflac (flac/->MetaflacOpts "C:\\opt\\flac\\metaflac.exe"))

(defn update-data-fast [file-data path]
  {:path path
   :tags (tool/read-tags! metaflac path)})

(defn update-data [file-data path]
  (let [file-hash (hash/sha1 path)]
    (if (= file-hash (:hash file-data))
      file-data
      {:path path
       :hash file-hash
       :tags (tool/read-tags! metaflac path)})))

(defn update-file! [path]
  (swap! data update (str path) update-data-fast (str path))
  nil)

(defn update-dir! [dir]
  (let [files (into [] (comp (filter file/file?)
                             (filter #(-> % str (str/ends-with? ".flac"))))
                       (file/walk dir))]
    (doseq [file files]
      (update-file! file))))

(defn lookup [path]
  (get @data path))

(defn store! []
  (with-open [writer (io/writer "D:\\mulima.edn")]
    (binding [*out* writer]
      (pr @data))))

(defn load! []
  (with-open [reader (java.io.PushbackReader. (io/reader "D:\\mulima.edn"))]
    (binding [*read-eval* false]
      (reset! data (read reader)))))

#_(load!)
#_(store!)
#_(reset! data {})
#_(println @data)
#_(update-file! "D:\\converted\\flac\\Phil Collins\\Face Value\\D01T01.flac")
#_(update-dir! "D:\\converted\\flac\\Phil Collins")
#_(update-dir! "D:\\converted\\flac")
#_(lookup "D:\\converted\\flac\\Phil Collins\\Face Value\\D01T01.flac")
#_(lookup "D:\\converted\\flac\\Phil Collins\\Tarzan\\D01T01.flac")
#_(file/write-str "D:\\mulima.edn" (str @data))
#_(into [] (comp (filter #(= "Bon Iver" (get-in [:tags :artist] %))))
           (vals @data))
