(ns mulima.tag.generic
  (:require [ike.cljj.file :as file]
            [clojure.string :as string]))

(defn- extension [path]
  (-> path file/as-path .getFileName str (string/split #"\.") last))

(defmulti parse* extension)
(defmulti emit* (fn [path _] (extension path)))
