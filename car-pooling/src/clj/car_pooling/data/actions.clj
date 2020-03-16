(ns car-pooling.data.actions
  (:require [car-pooling.data.core :as db]))

  (defn load-cars [cars]
    (swap! db/*data* assoc :cars cars))
