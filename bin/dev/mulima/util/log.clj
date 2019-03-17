(ns mulima.util.log
  (:require [])
  (:import [org.apache.logging.log4j Level Logger LogManager Marker MarkerManager]
           [org.apache.logging.log4j.message Message MapMessage]
           [java.util Map]))


(def levels
  {:trace Level/TRACE
   :debug Level/DEBUG
   :info Level/INFO
   :warn Level/WARN
   :error Level/ERROR})

(defn log*
  ""
  ([ns-name level message]
   (let [logger (LogManager/getLogger ns-name)
         level (get levels level)
         message (MapMessage. message)]
     (if (.isEnabled logger level message)
       (.log logger level nil message))))
  ([ns-name level message throwable]
   (let [logger (LogManager/getLogger ns-name)
         level (get levels level)
         message (MapMessage. message)]
     (if (.isEnabled logger level message throwable)
       (.log logger level nil message throwable)))))

(defmacro log
  ""
  ([level message]
   `(log* ~(str ~*ns*) ~level ~message))
  ([level message throwable]
   `(log* ~(str ~*ns*) ~level)))
