(ns dev
  (:require [mulima.core :as core]
            [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl :as repl :refer [set-init start stop reset]]))

(defn dev-system [_]
  (component/system-map
    :db (core/new-database "datomic:mem://mulima")))

(repl/set-init dev-system)
