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

;; TODO translate the xml form into what I expect from json or edn
(defn- parse-xml
  [path-str]
  (with-open [rdr (io/reader path-str)]
    (xml/parse rdr)))

(def parsers
  {"xml" parse-xml
   "json" parse-json
   "edn" parse-edn})

(defn parse
  [path-str]
  (get parsers (suffix path-str)))

(defn normalize
  [data]
  nil)

(defn denormalize
  [data]
  nil)
