(ns mulima.core
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [datomic.api :as datomic]
            [ike.cljj.file :as file])
  (:import [java.nio.file Files LinkOption]
           [java.util Date]))

(def metadata-schema [{:db/ident :file/path
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/unique :db.unique/identity
                       :db/doc "Path to a file"}

                      {:db/ident :file/size
                        :db/valueType :db.type/long
                        :db/cardinality :db.cardinality/one
                        :db/doc "Size of the file"}

                      {:db/ident :file/modified
                       :db/valueType :db.type/instant
                       :db/cardinality :db.cardinality/one
                       :db/doc "Time the file was last modified"}])

(defrecord Database [uri connection]
  component/Lifecycle
  (start [db]
    (println "Starting DB")
    (datomic/create-database (:uri db))
    (let [conn (datomic/connect (:uri db))]
      (datomic/transact conn metadata-schema)
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
