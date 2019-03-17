(ns mulima.tag.flac
  (:require [mulima.tag.generic :as generic]
            [me.raynes.conch :as sh])
  (:import [java.time Instant LocalDate]
           [java.time.format DateTimeFormatterBuilder]
           [java.time.temporal ChronoField]
           [java.util Date]))

(def ^:private tag-pattern #"(?m)^\s*comment\[[0-9]+\]:\s+(\w+)=(.*)\s*$")

(def ^:private vorbis-tags
  {"ALBUM" :mulima.tag/album
   "ALBUMSORT" :mulima.tag/album-sort
   "LABEL" :mulima.tag/record-label
   "CATALOGNUMBER" :mulima.tag/catalog-number
   "ORIGINALDATE" :mulima.tag/date-release
   "DATE" :mulima.tag/date-logical
   "ARTIST" :mulima.tag/artist
   "ARTISTSORT" :mulima.tag/artist-sort
   "COMPOSER" :mulima.tag/composer
   "COMPOSERSORT" :mulima.tag/composer-sort
   "DISCNUMBER" :mulima.tag/disc-number
   "TRACKNUMBER" :mulima.tag/track-number
   "TITLE" :mulima.tag/title
   "TITLESORT" :mulima.tag/title-sort
   "GENRE" :mulima.tag/genre})

(defn- cmd!
  ([path args] (cmd! path "" args))
  ([path in args]
   (let [out (java.io.StringWriter.)
         err (java.io.StringWriter.)]
     (try
       (sh/run-command path args {:in in :out out :err err})
       (str out)
       (catch Exception e
         (do
           (println (str out))
           (println (str err))
           (throw e)))))))

(def date-format (-> (DateTimeFormatterBuilder.)
                     (.appendPattern "yyyy[-MM[-dd]]")
                     (.parseDefaulting ChronoField/MONTH_OF_YEAR 1)
                     (.parseDefaulting ChronoField/DAY_OF_MONTH 1)
                     (.parseDefaulting ChronoField/HOUR_OF_DAY 0)
                     (.parseDefaulting ChronoField/MINUTE_OF_HOUR 0)
                     (.parseDefaulting ChronoField/SECOND_OF_MINUTE 0)
                     (.parseDefaulting ChronoField/MILLI_OF_SECOND 0)
                     (.parseDefaulting ChronoField/OFFSET_SECONDS 0)
                     (.toFormatter)))

(defn- update-contains [m k f & more]
  (if (contains? m k)
    (apply update m k f more)
    m))

(defn- fix-types [tags]
  (-> tags
      (update-contains :mulima.tag/disc-number (fn [v] (if v (Integer/parseInt v))))
      (update-contains :mulima.tag/track-number (fn [v] (if v (Integer/parseInt v))))
      (update-contains :mulima.tag/date-logical (fn [v] (if v (Date/from (Instant/from (.parse date-format v))))))
      (update-contains :mulima.tag/date-release (fn [v] (if v (Date/from (Instant/from (.parse date-format v))))))))

(defmethod generic/parse* "flac" [path]
  (->> (cmd! "C:\\opt\\flac\\metaflac" ["--list" "--block-type=VORBIS_COMMENT" path])
       (re-seq tag-pattern)
       (map (fn [[m k v]] [(get vorbis-tags k) v]))
       (filter (fn [[k v]] (and k (seq v))))
       (into {})
       (fix-types)))
