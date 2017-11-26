(ns mulima.core
  (:require [com.stuartsierra.component :as component]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [datomic.api :as datomic]
            [ike.cljj.file :as file]
            [ike.cljj.stream :as stream]
            [mulima.tag :as tag]
            [clojure.core.async :as async])
  (:import [java.nio.file Files LinkOption]
           [java.util Date]
           [java.io PushbackReader]))

(def schema (with-open [reader (-> "schema.edn"
                                   io/resource
                                   io/reader
                                   PushbackReader.)]
              (edn/read reader)))

(defrecord Database [uri connection]
  component/Lifecycle
  (start [db]
    (println "Starting DB")
    (datomic/create-database (:uri db))
    (let [conn (datomic/connect (:uri db))]
      @(datomic/transact conn schema)
      (assoc db :connection conn)))
  (stop [db]
    (println "Stopping DB")
    (datomic/release (:connection db))))

(defn new-database [uri]
  (map->Database {:uri uri}))

(defn extension-match [ext]
  (fn [path]
    (let [real-ext (-> path file/as-path .getFileName str (string/split #"\.") last)]
      (= ext real-ext))))

(defn scan-file [file]
  (let [tag-info (tag/parse file)
        file-info {:mulima.file/path (str file)
                   :mulima.file/size (file/size file)
                   :mulima.file/modified (Date/from (.toInstant (Files/getLastModifiedTime file (into-array LinkOption []))))}]
    (merge file-info tag-info)))

(defn scan-dir [database dir]
  (with-open [stream (file/walk dir)]
    (let [xf (comp (filter file/file?)
                   (filter (extension-match "flac"))
                   (map scan-file)
                   (partition-all 25))
          tx-chan (async/chan)]
      (async/pipeline-blocking 1 tx-chan xf (async/to-chan (into [] stream)))
      (async/go-loop []
        (when-let [txs (async/<! tx-chan)]
          @(datomic/transact (:connection database) txs)
          (recur))))))
