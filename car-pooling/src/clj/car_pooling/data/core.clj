(ns car-pooling.data.core
  (:require
    [mount.core :refer [defstate]]))

(defn- initialise-data []
  {:cars []})

(defstate ^:dynamic *data*
  :start  (atom (initialise-data))
  :stop   ())
