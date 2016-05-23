(ns mulima.meta
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.data.xml :as xml]))

(defn- suffix
  [path-str]
  (-> path-str
      (io/file)
      (.getName)
      (string/split #"\.")
      (last)))

(defn- parse-edn
  [path-str]
  (with-open [is (io/input-stream path-str)]
    (edn/read is)))

(defn- parse-json
  [path-str]
  (with-open [rdr (io/reader path-str)]
    (json/read rdr)))

(defmulti parse-xml-element :tag)

(defmethod parse-xml-element :tag [element]
  (let [{:keys [name value]} (:attrs element)]
    [(keyword name) value]))

(defmethod parse-xml-element :startPoint [element]
  (let [time (-> element :attrs :time)]
    [:cues [nil time nil]]))

(defmethod parse-xml-element :track [element]
  (let [xform (map parse-xml-element)]
    (into {} xform (:content element))))

(defmethod parse-xml-element :disc [element]
  (let [content (:content element)
        xform (map parse-xml-element)
        tags (filter #(= :tag (:tag %)) content)
        parsed-tags (into {} xform tags)
        tracks (filter #(= :track (:tag %)) content)
        parsed-tracks (into [] xform tracks)
        base {:children parsed-tracks}]
    (merge parsed-tags base)))

(defmethod parse-xml-element :album [element]
  (let [content (:content element)
        xform (map parse-xml-element)
        tags (filter #(= :tag (:tag %)) content)
        parsed-tags (into {} xform tags)
        tracks (filter #(= :disc (:tag %)) content)
        parsed-tracks (into [] xform tracks)
        base {:children parsed-tracks}]
    (merge parsed-tags base)))

(defn- parse-xml
  [path-str]
  (let [contents (-> path-str slurp xml/parse-str)]
    (parse-xml-element contents)))

(def parsers
  {"xml" parse-xml
   "json" parse-json
   "edn" parse-edn})

(defn parse
  [path-str]
  (let [parser (get parsers (suffix path-str))]
    (parser path-str)))

(defn normalize
  [data]
  nil)

(defn denormalize
  [data]
  nil)
