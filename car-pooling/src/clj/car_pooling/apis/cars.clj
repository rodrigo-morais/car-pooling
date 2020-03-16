(ns car-pooling.apis.cars
  (:require [car-pooling.data.actions :as ac]))

  (defn load-cars [cars]
    (ac/load-cars cars)
    {:status 200})
