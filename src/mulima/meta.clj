(ns mulima.meta
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.data.xml :as xml]
            [clojure.spec :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Validation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn cuepoint? [value] true)

(s/def ::cues (s/cat :pregap cuepoint? :start cuepoint? :end cuepoint?))
(s/def ::tags map?)
(s/def ::children (s/* ::metadata))
(s/def ::metadata (s/keys :opt [::cues ::children ::tags]))

#_(s/explain ::metadata {::tags {:album "yo" :track-number 1} ::cues ["01" "02" "03"] ::children []})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; XML parsing
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
(defn- suffix
  [path-str]
  (-> path-str
      (io/file)
      (.getName)
      (string/split #"\.")
      (last)))

(defmulti parse* suffix)

(defmethod parse* "xml" [path-str]
  (let [contents (-> path-str slurp xml/parse-str)]
    (parse-xml-element contents)))

(defmethod parse* "json" [path-str]
  (with-open [rdr (io/reader path-str)]
    (json/read rdr)))

(defmethod parse* "edn" [path-str]
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
