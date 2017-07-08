(defproject mulima "0.2.0-SNAPSHOT"
  :description "Music Library Manager"
  :url "https://github.com/ajoberstar/mulima"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.async "0.3.443"]
                 [org.clojure/tools.logging "0.4.0"]

                 ;; reloaded
                 [org.clojure/tools.namespace "0.2.11"]

                 ;; util
                 [org.ajoberstar/ike.cljj "0.3.0"]
                 [com.rpl/specter "1.0.2"]
                 [commons-codec "1.10"]

                 ;; io
                 [me.raynes/conch "0.8.0"]
                 [org.clojure/data.xml "0.0.8"]]
  :plugins [[lein-ancient "0.6.10"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}})
