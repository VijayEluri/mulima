(ns mulima.core
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [datomic.api :as d]))

(defn connect [port name]
  (let [uri (str "datomic:free://localhost:" port "/name")]
    (d/create-database uri)
    (d/connect uri)))

(def metadata-schema [{:db/ident :file/path
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "Path to a file"}

                      {:db/ident :file/tag
                        :db/valueType :db.type/string
                        :db/cardinality :db.cardinality/one
                        :db/doc "A tag name"}

                      {:db/ident :file/value
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/many
                       :db/doc "A tag value"}])
