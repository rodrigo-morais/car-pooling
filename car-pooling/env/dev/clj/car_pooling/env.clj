(ns car-pooling.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [car-pooling.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[car-pooling started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[car-pooling has shut down successfully]=-"))
   :middleware wrap-dev})
