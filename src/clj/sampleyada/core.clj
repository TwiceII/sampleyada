(ns sampleyada.core
  (:require [bidi.bidi :as bidi]
            [yada.yada :as yada]
            [yada.resources.classpath-resource :refer [new-classpath-resource]])
  (:gen-class))


(def ^:const default-port 83)

;; aleph-server
(defonce server (atom nil))


(def bidi-routes
  ["" {[] (yada/resource {:id ::index
                          :produces {:media-type "text/plain"}
                          :methods {:get
                                     {:response (fn[ctx](-> "hey there!"))}}})
       ;; works
       "/open" (yada/resource
                 {:id ::open-resource
                  :produces "text/plain"
                  :methods {:get
                            {:response (fn [ctx] (-> "open resource"))}}})

       ;; doesn't work
       "/private" (yada/resource
                   {:id ::private-resource
                    :produces "text/plain"
                    :methods {:get
                            {:response (fn [ctx] (-> "private resource"))}}

                    ;; from manual docs
                    ;; https://yada.juxt.pro/manual/100_security.md
                    :access-control
                    {:scheme :cookie
                     :cookie "session"
                     :verify (fn [cookie]
                               (-> nil))}

                    })

       "" (new-classpath-resource "public")
       }])

;;;
;;; Server functions
;;;
(defn start-web-server!
  []
  (println "start-web-server!")
  (let [port default-port
        listener (yada/listener bidi-routes {:port port})]
    (reset! server listener)))


(defn restart!
  []
  ((:close @server))
  (start-web-server! nil))


(defn start-app!
  []
  (do
    (println "App started...")
    (start-web-server!)))


(defn -main [& args]
  (start-app!))
