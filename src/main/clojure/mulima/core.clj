(ns mulima.core
  (:require [com.stuartsierra.component :as component]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [datomic.api :as datomic]
            [ike.cljj.file :as file])
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
      (datomic/transact conn schema)
      (assoc db :connection conn)))
  (stop [db]
    (println "Stopping DB")
    (datomic/release (:connection db))))

(defn new-database [uri]
  (map->Database {:uri uri}))

(defn scan-file [file]
  {:file/path (str file)
   :file/size (file/size file)
   :file/modified (Date/from (.toInstant (Files/getLastModifiedTime file (into-array LinkOption []))))})

(defn scan-dir [database dir]
  (with-open [stream (file/walk dir)]
    (let [txs (into [] (map scan-file) stream)]
      (datomic/transact (:connection database) txs))))
