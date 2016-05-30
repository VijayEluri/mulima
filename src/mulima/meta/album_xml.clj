(ns mulima.meta.album-xml
  (:require [mulima.meta.generic :refer [parse* denormalize]]
            [clojure.java.io :as io]
            [clojure.data.xml :as xml]
            [clojure.edn :as edn]))

(defmulti parse-value (fn [name _] name))
(defmethod parse-value :discNumber [_ value] (edn/read-string value))
(defmethod parse-value :trackNumber [_ value] (edn/read-string value))
(defmethod parse-value :default [_ value] value)

(defmulti parse-xml-element :tag)

(defn- parse-child-xml-elements [tag]
  (comp (filter #(= tag (:tag %)))
        (map parse-xml-element)))

(defn- parse-parent-xml-element [element child-tag]
  (let [content (:content element)
        tags (into {} (parse-child-xml-elements :tag) content)
        children (into [] (parse-child-xml-elements child-tag) content)]
    {:mulima.meta/tags tags :mulima.meta/children children}))

(defmethod parse-xml-element :tag [element]
  (let [{:keys [name value]} (:attrs element)
        name (keyword name)
        value (parse-value name value)]
    [name value]))

(defmethod parse-xml-element :startPoint [element]
  (let [time (-> element :attrs :time)]
    [nil time nil]))

(defmethod parse-xml-element :track [element]
  (let [content (:content element)
        tags (into {} (parse-child-xml-elements :tag) content)
        cues (let [xf (parse-child-xml-elements :startPoint)]
               (first (eduction xf content)))]
    {:mulima.meta/tags tags :mulima.meta/cues cues}))

(defmethod parse-xml-element :disc [element]
  (parse-parent-xml-element element :track))

(defmethod parse-xml-element :album [element]
  (parse-parent-xml-element element :disc))

(defn- start-point [track]
  [(get-in track [:mulima.meta/tags :discNumber])
   (get-in track [:mulima.meta/tags :trackNumber])
   (get-in track [:mulima.meta/cues 1])])

(defn- into-nested [m p]
  (let [ks (butlast p)
        v (last p)]
    (assoc-in m ks v)))

(defn- start-points [data]
  (let [xf (map start-point)]
    (transduce xf (completing into-nested) {} data)))

(defmethod parse* "album.xml" [path-str]
  (let [contents (-> path-str slurp xml/parse-str)
        data (-> contents parse-xml-element denormalize)
        points (start-points data)
        end-point (fn [track]
                    (let [dnum (get-in track [:mulima.meta/tags :discNumber])
                          tnum (get-in track [:mulima.meta/tags :trackNumber])
                          epoint (get-in points [dnum (inc tnum)])]
                      (assoc-in track [:mulima.meta/cues 2] epoint)))]
    (into [] (map end-point) data)))
