(ns mulima.meta
  (:require [mulima.meta.generic :as meta]
            [mulima.meta.album-xml]
            [mulima.meta.metadata-edn]
            [clojure.edn :as edn]
            [clojure.spec :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generic Parsing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn parse
  [path-str]
  (let [parsed (meta/parse* path-str)]
    (if (s/valid? ::metadata parsed)
      parsed
      (s/explain ::metadata parsed))))

(defn parse
  [path-str]
  (meta/parse* path-str))

(defn emit
  [path-str data]
  nil)

#_(parse "C:\\Users\\andre\\projects\\mulima\\album.xml")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
#_(def normalize meta/normalize)

#_(def denormalize meta/denormalize)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Specs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- cuepoint-frames? [value]
  (if-let [groups (re-matches #"(\d+):(\d{2}):(\d{2})" value)]
    (let [[minutes seconds frames] (map edn/read-string (rest groups))]
      (and (<= 0 minutes)
           (<= 0 seconds 59)
           (<= 0 frames 74)))))

(defn- cuepoint-time? [value]
  (if-let [groups (re-matches #"(\d+):(\d{2}).(\d{3})" value)]
    (let [[minutes seconds millis] (map edn/read-string (rest groups))]
      (and (<= 0 minutes)
           (<= 0 seconds 59)
           (<= 0 millis 999)))))

(defn cuepoint? [value]
  (let [pred (some-fn cuepoint-frames? cuepoint-time?)]
    (boolean (pred value))))

;; TODO remove this
(defn cuepoint? [value] true)

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

(comment
  (s/fdef normalize)
  :args (s/cat :data ::metadata)
  :ret ::metadata

  (s/fdef denormalize
    :args (s/cat :data ::metadata)
    :ret ::metadata))
