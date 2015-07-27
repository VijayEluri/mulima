(defproject mulima "0.2.0-SNAPSHOT"
  :description "Music Library Manager"
  :url "https://github.com/ajoberstar/mulima"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.logging "0.3.1"]

                 ;; reloaded
                 [org.clojure/tools.namespace "0.2.10"]

                 ;; io
                 [info.hoetzel/clj-nio2 "0.1.1"]
                 [me.raynes/conch "0.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.xml "0.0.8"]]
  :plugins [[cider/cider-nrepl "0.7.0"]
            [lein-ancient "0.6.7"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.7.0"]]}})
