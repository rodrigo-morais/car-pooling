(ns car-pooling.data.actions
  (:require [car-pooling.data.core :as db]))

  (defn remove-available-cars [cars]
    (remove #(= (:available %) true) cars))

  (defn load-cars [cars]
    (let [current-cars  (remove-available-cars (:cars @db/*data*))
          new-cars      (map (fn [car] (assoc car :available true)) cars)]
      (swap! db/*data* assoc :cars (concat current-cars new-cars))))

  (defn- get-journeys-ids []
    (vec (map (fn [journey] (get journey :id)) (:journeys @db/*data*))))

  (defn journey-exist? [id]
    (boolean (some #(= id %) (get-journeys-ids))))

  (defn add-journey [journey]
    (swap! db/*data* assoc :journeys (conj (:journeys @db/*data*) (assoc journey :car nil))))

  (defn drop-off-journey [id]
    (swap! db/*data* assoc :journeys (remove #(= (:id %) id) (:journeys @db/*data*))))
