(ns dev
  (:require [mulima.core :as core]
            [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl :as repl :refer [set-init start stop reset]]))

(defn dev-system [_]
  (component/system-map))
    

(repl/set-init dev-system)
