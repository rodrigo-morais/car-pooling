(ns car-pooling.apis.cars
  (:require [car-pooling.data.actions :as ac]))

  (defn load-cars [cars]
    (if (ac/cars-exist? cars)
      {:status 400 :body {}}
      (do
        (ac/load-cars cars)
        {:status 200})))
