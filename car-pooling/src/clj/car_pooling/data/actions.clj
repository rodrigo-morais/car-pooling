(ns car-pooling.data.actions
  (:require [car-pooling.data.core :as db]))

  (defn load-cars [cars]
    (swap! db/*data* assoc :cars cars))

  (defn add-journey [journey]
    (swap! db/*data* assoc :journeys (conj (:journeys @db/*data*) journey)))
