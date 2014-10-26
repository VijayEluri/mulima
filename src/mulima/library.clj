(ns mulima.library)

(defprotocol LibraryScanner
  (lookup-all [lib])
  (lookup-new [lib])
  (lookup [lib id]))

(defprotocol LibraryManager
  (process [lib & libs])
  (update [lib & libs]))
