(ns user
  (:require [com.stuartsierra.component.user-helpers :as helpers]))

;; proto-repl uses a user/reset function when it refreshes
(def reset helpers/reset)
