(ns mulima.core
  (:require [mulima.meta :as meta]
            [ike.cljj.file :as file]
            ;; just for the collreduce impl of stream
            [ike.cljj.stream]))

(defn- incomplete-meta?
  [meta]
  (not-every? #(get-in % [:mulima.meta/tags :title]) meta))

(defn find-incomplete-meta
  [root-dir]
  (let [children (file/walk root-dir)
        xf (comp (filter (fn [file]
                           (let [name (-> file .getFileName str)]
                            (= name "album.xml"))))
                 (map (fn [file] [file (meta/parse file)]))
                 (filter (fn [[_ meta]] (incomplete-meta? meta)))
                 (map first)
                 (map str))]
    (into [] xf children)))

#_(doseq [file (find-incomplete-meta "F:\\Music\\flac-rips2")] (println file))
#_(meta/parse "F:\\Music\\flac-rips2\\Adele\\19\\album.xml")
#_(println *e)
