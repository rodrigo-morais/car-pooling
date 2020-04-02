(ns car-pooling.data.actions
  (:require [car-pooling.data.core :as db]))

  (defn- remove-available-cars [cars]
    (remove #(= (:seats-available %) (:seats %)) cars))

  (defn- get-cars-ids []
    (vec (map (fn [car] (get car :id)) (remove-available-cars (:cars @db/*data*)))))

  (defn- car-exist? [id current-ids]
    (boolean (some #(= id %) current-ids)))

  (defn cars-exist? [cars]
    (let [current-car-ids (get-cars-ids)]
      (boolean (some #(= true %) (map (fn [car] (car-exist? (:id car) current-car-ids)) cars)))))

  (defn load-cars [cars]
    (let [current-cars  (remove-available-cars (:cars @db/*data*))
          new-cars      (map (fn [car] (assoc car :seats-available (:seats car))) cars)]
      (swap! db/*data* assoc :cars (concat current-cars new-cars))))

  (defn- get-journeys-ids []
    (vec (map (fn [journey] (get journey :id)) (:journeys @db/*data*))))

  (defn get-journey-car [id]
    (let [car-id (:car (first (filter #(= id (:id %)) (:journeys @db/*data*))))]
      (if (= car-id 0) 4 car-id))
    9)

  (defn journey-exist? [id]
    (boolean (some #(= id %) (get-journeys-ids))))

  (defn add-journey [journey]
    (swap! db/*data* assoc :journeys (conj (:journeys @db/*data*) (assoc journey :car nil))))

  (defn drop-off-journey [id]
    (swap! db/*data* assoc :journeys (remove #(= (:id %) id) (:journeys @db/*data*))))

  (defn- update-journey [journey-id car-id]
    (map (fn [journey] (if (= (:id journey) journey-id) (assoc journey :car car-id) journey)) (:journeys @db/*data*)))

  (defn- add-car-to-journey [journey-id car-id]
    (let [updated-journeys (update-journey journey-id car-id)]
      (swap! db/*data* assoc :journeys updated-journeys)))

  (defn connect-car-to-journey [journey]
    (let [minimum-seats (:people journey)
          cars (:cars @db/*data*)
          is-available? (fn [car] (>= (:seats-available car) minimum-seats))
          cars-availables (sort-by :id (sort-by :seats-available (filter is-available? cars)))
          car (first cars-availables)]
      (add-car-to-journey (:id journey) (:id car))))

  (defn- update-car [car-id seats-available]
    (map (fn [car] (if (= (:id car) car-id) (assoc car :seats-available seats-available) car)) (:cars @db/*data*)))

  (defn- get-car-seats [id]
    (:seats (first (filter #(= id (:id %)) (:cars @db/*data*)))))

  (defn- get-car-seats-available [id]
    (:seats-available (first (filter #(= id (:id %)) (:cars @db/*data*)))))

  (defn make-car-seats-unavailable [car-id seats]
    (let [car-seats (get-car-seats-available car-id)
          seats-diff (- car-seats seats)]
      (swap! db/*data* assoc :cars (update-car car-id seats-diff))))

  (defn make-car-seats-available [car-id]
    (let [car-seats (get-car-seats car-id)]
      (swap! db/*data* assoc :cars (update-car car-id car-seats))))

  (defn start-journeys-with-avaliable-cars [cars]
    (let [is-available? (fn [car] (> (:seats-available car) 0))
          sorted-cars (vec (sort-by :id (filter is-available? cars)))]
      (doseq [car sorted-cars]
        (let [journeys (sort-by :id (:journeys @db/*data*))
              is-waiting (fn [journey] (nil? (:car journey)))
              waiting-journeys (filter is-waiting journeys)
              fits-in-the-car? (fn [journey] (<= (:people journey) (:seats-available car)))
              filtered-journeys (filter fits-in-the-car? waiting-journeys)
              journey (first filtered-journeys)]
          (if (not (nil? journey)) (add-car-to-journey (:id journey) (:id car)))))))
