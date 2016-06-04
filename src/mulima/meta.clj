(ns mulima.meta
  (:require [mulima.meta.generic :as meta]
            [mulima.meta.album-xml]
            [mulima.meta.metadata-edn]
            [clojure.edn :as edn]
            [clojure.spec :as s]
            [ike.cljj.file :as file]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generic Parsing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn parse
  "Parses the given path as metadata."
  [path-str]
  (let [parsed (meta/parse* (file/as-path path-str))]
    (if (s/valid? (s/+ ::metadata) parsed)
      parsed
      (s/explain (s/+ ::metadata) parsed))))

(defn emit
  "Emits the given data to the provided path."
  [path-str data]
  (meta/emit* (file/as-path path-str) data))

#_(parse "C:\\Users\\andre\\projects\\mulima\\album.xml")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Specs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- cuepoint-frames?
  "Validates whether the value matches the frame format (mm:ss:ff)."
  [value]
  (if value
    (if-let [groups (re-matches #"(\d+):(\d{2}):(\d{2})" value)]
      (let [[minutes seconds frames] (map #(Integer/parseInt %) (rest groups))]
        (and (<= 0 minutes)
             (<= 0 seconds 59)
             (<= 0 frames 74))))))

(defn- cuepoint-time?
  "Validates whether the value matches the millis format (mm:ss.SSS)"
  [value]
  (if value
    (if-let [groups (re-matches #"(\d+):(\d{2}).(\d{3})" value)]
      (let [[minutes seconds millis] (map #(Integer/parseInt %) (rest groups))]
        (and (<= 0 minutes)
             (<= 0 seconds 59)
             (<= 0 millis 999))))))

(defn cuepoint?
  "Validates whether the value matches either the frame or millis format."
  [value]
  (let [pred (some-fn cuepoint-frames? cuepoint-time?)]
    (or (nil? value) (-> value pred boolean))))

(s/def ::cues (s/cat :pregap cuepoint? :start cuepoint? :end cuepoint?))
(s/def ::source string?)
(s/def ::tags map?)
(s/def ::children (s/* ::metadata))
(s/def ::metadata (s/keys :opt [::cues ::source ::children ::tags]))

(s/fdef parse
  :args (s/cat :path-str string?)
  :ret (s/+ ::metadata))

(s/fdef emit
  :args (s/cat :path-str string? :data ::metadata))
