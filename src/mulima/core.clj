(ns mulima.core
  (:require [mulima.meta :as meta]
            [ike.cljj.file :as file]
            ;; just for the collreduce impl of stream
            [ike.cljj.stream :as stream]
            [clojure.string :as str])
  (:import [org.apache.commons.codec.binary Base64]
           [org.apache.commons.codec.digest DigestUtils]))

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

(defn- album-dir?
  [path]
  (and (file/dir? path)
       (file/exists? (.resolve path "album.xml"))))

(defn- no-artwork?
  [dir]
  (let [children (file/list dir)
        xf (comp (map #(-> % .getFileName str (str/split #"\.") last))
                 (filter #{"jpg" "png"}))]
    (empty? (sequence xf (stream/stream-seq children)))))

(defn find-missing-artwork
  [root-dir]
  (let [children (file/walk root-dir)
        xf (comp (filter album-dir?)
                 (filter no-artwork?)
                 (map str))]
    (into [] xf children)))

#_(doseq [file (find-incomplete-meta "D:\\originals\\flac-rips")]
    (println "Incomplete: " file)
    (let [siblings (file/walk (.getParent (file/as-path file)))
          xf (comp (filter #(-> % .getFileName str (str/split #"\.") last (= "cue")))
                   (map meta/parse)
                   (mapcat identity))
          cue-data (into [] xf siblings)]
      (meta/emit file cue-data)))
#_(doseq [file (find-missing-artwork "D:\\originals\\flac-rips")] (println file))
#_(meta/parse "C:\\Users\\andre\\Music\\originals\\Prince\\Prince\\album.xml")
#_(let [parsed (meta/parse "C:\\Users\\andre\\Music\\originals\\Prince\\Prince\\album.xml")
        emitted (do
                  (meta/emit "C:\\Temp\\album.xml" parsed)
                  (meta/parse "C:\\Temp\\album.xml"))]
    (println parsed)
    (println emitted)
    (= parsed emitted))
#_(println *e)

(defn offset [track]
  (let [groups (re-matches #"(\d+):(\d{2}):(\d{2})" (-> track :mulima.meta/cues second))]
    (let [[minutes seconds frames] (map #(Integer/parseInt %) (rest groups))]
      (+ (* 60 75 minutes) (* 75 seconds) frames 150))))

(defn disc-id [cue]
  (let [first-track (-> cue first :mulima.meta/tags :track-number)
        last-track (-> cue last :mulima.meta/tags :track-number)
        offsets (take 100 (concat [218092] (map offset cue) (repeat 0)))
        parts (apply vector (format "%02X" first-track) (format "%02X" last-track) (map #(format "%08X" %) offsets))
        input (str/join parts)
        digest (Base64/encodeBase64String (DigestUtils/sha1 input))]
    (reduce (fn [acc [old new]] (str/replace acc old new)) digest {"+" ".", "/" "_", "=" "-"})))

#_(disc-id (meta/parse "D:\\originals\\flac-rips\\Adele\\25\\Adele - 25.cue"))
