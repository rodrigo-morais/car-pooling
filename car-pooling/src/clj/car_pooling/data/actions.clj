(ns car-pooling.data.actions
  (:require [car-pooling.data.core :as db]))

  (defn load-cars [cars]
    (swap! db/*data* assoc :cars cars))

  (defn- get-journeys-ids []
    (vec (map (fn [journey] (get journey :id)) (:journeys @db/*data*))))

  (defn journey-exist? [id]
    (boolean (some #(= id %) (get-journeys-ids))))

  (defn add-journey [journey]
    (swap! db/*data* assoc :journeys (conj (:journeys @db/*data*) journey)))
