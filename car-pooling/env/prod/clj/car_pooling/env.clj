(ns car-pooling.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[car-pooling started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[car-pooling has shut down successfully]=-"))
   :middleware identity})
