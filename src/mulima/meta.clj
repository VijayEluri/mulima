(ns mulima.meta
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.data.xml :as xml]
            [clojure.spec :as s]
            [ike.cljj.file :as file]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; album.xml parsing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmulti parse-xml-element :tag)

(defn- parse-child-xml-elements [tag]
  (comp (filter #(= tag (:tag %)))
        (map parse-xml-element)))

(defn- parse-parent-xml-element [element child-tag]
  (let [content (:content element)
        tags (into {} (parse-child-xml-elements :tag) content)
        children (into [] (parse-child-xml-elements child-tag) content)]
    {::tags tags ::children children}))

(defmethod parse-xml-element :tag [element]
  (let [{:keys [name value]} (:attrs element)]
    [(keyword name) value]))

(defmethod parse-xml-element :startPoint [element]
  (let [time (-> element :attrs :time)]
    [nil time nil]))

(defmethod parse-xml-element :track [element]
  (let [content (:content element)
        tags (into {} (parse-child-xml-elements :tag) content)
        cues (let [xf (parse-child-xml-elements :startPoint)]
               (first (eduction xf content)))]
    {::tags tags ::cues cues}))

(defmethod parse-xml-element :disc [element]
  (parse-parent-xml-element element :track))

(defmethod parse-xml-element :album [element]
  (parse-parent-xml-element element :disc))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generic Parsing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- file-name
  [path-str]
  (-> path-str file/path .getFileName str))

(defmulti parse* file-name)

(defmethod parse* "album.xml" [path-str]
  (let [contents (-> path-str slurp xml/parse-str)]
    (parse-xml-element contents)))

(defmethod parse* "metadata.edn" [path-str]
  (with-open [is (io/input-stream path-str)]
    (edn/read is)))

(defn parse
  [path-str]
  (let [parsed (parse* path-str)]
    (if (s/valid? ::metadata parsed)
      parsed
      (s/explain ::metadata parsed))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Normalization / Denormalization
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn normalize
  [data]
  nil)

(defn- denormalize*
  [data]
  (if (contains? data ::children)
    (let [children (::children data)
          tags (::tags data)
          merge-tags (fn [meta] (update meta ::tags #(merge tags %)))
          xf (comp (map merge-tags)
                   (mapcat denormalize*))]
      (eduction xf children))
    [data]))

(defn denormalize
  [data]
  (->> data
       denormalize*
       (into [])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Specs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- cuepoint-frames? [value]
  (if-let [groups (re-matches #"(\d)+:(\d{2}):(\d{2})" value)]
    (let [[_ minutes seconds frames] (map edn/read-string (rest groups))]
      (and (< 0 minutes)
           (<= 0 seconds 59)
           (<= 0 frames 74)))))

(defn- cuepoint-time? [value]
  (if-let [groups (re-matches #"(\d)+:(\d{2}).(\d{3})" value)]
    (let [[_ minutes seconds millis] (map edn/read-string (rest groups))]
      (and (< 0 minutes)
           (<= 0 seconds 59)
           (<= 0 millis 999)))))

(defn cuepoint? [value]
  (let [pred (some-fn cuepoint-frames? cuepoint-time?)]
    (boolean (pred value))))

(s/def ::cues (s/cat :pregap cuepoint? :start cuepoint? :end cuepoint?))
(s/def ::source string?)
(s/def ::tags map?)
(s/def ::children (s/* ::metadata))
(s/def ::metadata (s/keys :opt [::cues ::source ::children ::tags]))

(s/fdef parse
  :args (s/cat :path-str string?)
  :ret ::metadata)

(s/fdef normalize
  :args (s/cat :data ::metadata)
  :ret ::metadata)

(s/fdef denormalize
  :args (s/cat :data ::metadata)
  :ret ::metadata)
