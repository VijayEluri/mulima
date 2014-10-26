(ns mulima.cache)

(defprotocol Cache
  (create [this dir])
  (read [this dir])
  (write [this dir]))
