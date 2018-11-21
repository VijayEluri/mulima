(ns mulima.meta.generic
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [clojure.string :refer [split]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generic Parsing/Emitting
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- file-ext
  [path]
  (-> path .getFileName str (split #"\.") last))

(defmulti parse*
  "Parses the given Path as metadata. Dispatches on the file name."
  file-ext)

(defmulti emit*
  "Emits the given data to the Path provided. Dispatches on the file name."
  (fn [path _] (file-ext path)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn normalize
  "Normalizes a sequence of metadata to return a single nested metadata with
  all common tags held at the root node."
  [data]
  (let [intersection (fn [a b]
                       (let [a-sub (select-keys a (keys b))
                             b-sub (select-keys b (keys a))
                             match (fn [x y] (if (= x y) x nil))]
                          (merge-with match a-sub b-sub)))
        shared (reduce intersection (map :mulima.meta/tags data))
        clean (into {} (remove (comp nil? second) shared))
        only (fn [tags]
               (let [dirty-keys (set/difference (set (keys tags)) (set (keys clean)))]
                 (select-keys tags dirty-keys)))
        xf (map (fn [meta] (update meta :mulima.meta/tags only)))]
    {:mulima.meta/tags clean
     :mulima.meta/children (into [] xf data)}))

(defn- denormalize*
  [data]
  (if (contains? data :mulima.meta/children)
    (let [children (:mulima.meta/children data)
          tags (:mulima.meta/tags data)
          merge-tags (fn [meta] (update meta :mulima.meta/tags #(merge tags %)))
          xf (comp (map merge-tags)
                   (mapcat denormalize*))]
      (eduction xf children))
    [data]))

(defn denormalize
  "Denormalizes a metadata with possible nesting (e.g. populated
  :mulima.meta/children). Returns a vector of metadata representing each leaf
  (i.e. track) with the tags of their previous parents included."
  [data]
  (into [] (denormalize* data)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Specs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(s/fdef normalize)
:args (s/cat :data (s/+ :mulima.meta/metadata))
:ret :mulima.meta/metadata

(s/fdef denormalize
  :args (s/cat :data :mulima.meta/metadata)
  :ret (s/+ :mulima.meta/metadata))

#_(let [normalized {:mulima.meta/tags {:album "Foxtrot" :artist "Genesis" :discNumber 1}
                    :mulima.meta/children [{:mulima.meta/tags {:trackNumber 1 :title "Watcher of the Skies"}
                                            :mulima.meta/cues [nil "00:00:00" "07:40:00"]}
                                           {:mulima.meta/tags {:trackNumber 2 :title "Get 'Em Out By Friday'"}
                                            :mulima.meta/cues [nil "07:40:00" "10:10:10"]}]}
        denormalized [{:mulima.meta/tags {:album "Foxtrot" :artist "Genesis" :discNumber 1 :trackNumber 1 :title "Watcher of the Skies"}
                       :mulima.meta/cues [nil "00:00:00" "07:40:00"]}
                      {:mulima.meta/tags {:album "Foxtrot" :artist "Genesis" :discNumber 1 :trackNumber 2 :title "Get 'Em Out By Friday'"}
                       :mulima.meta/cues [nil "07:40:00" "10:10:10"]}]]
    (println "Denormalize: " (denormalize normalized))
    (println "Normalize: " (normalize denormalized))
    (println "Round Trip: " (= denormalized (denormalize (normalize denormalized))))
    (println "Round Trip: " (= normalized (normalize (denormalize normalized)))))
