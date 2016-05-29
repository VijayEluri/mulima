(defproject mulima "0.2.0-SNAPSHOT"
  :description "Music Library Manager"
  :url "https://github.com/ajoberstar/mulima"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha3"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/tools.logging "0.3.1"]

                 ;; reloaded
                 [org.clojure/tools.namespace "0.2.11"]

                 ;; util
                 [ike/ike.cljj "0.2.2"]
                 [com.rpl/specter "0.10.0"]

                 ;; io
                 [me.raynes/conch "0.8.0"]
                 [org.clojure/data.xml "0.0.8"]]
  :plugins [[cider/cider-nrepl "0.12.0"]
            [lein-ancient "0.6.10"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}})
