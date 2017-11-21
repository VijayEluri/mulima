(ns mulima.core
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [datomic.api :as datomic]))

(defrecord Database [uri connection]
  component/Lifecycle
  (start [db]
    (println "Starting DB")
    (datomic/create-database (:uri db))
    (assoc db :connection (datomic/connect (:uri db))))
  (stop [db]
    (println "Stopping DB")
    (datomic/release (:connection db))))

(defn new-database [uri]
  (map->Database {:uri uri}))

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
