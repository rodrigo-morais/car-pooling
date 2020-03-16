(ns car-pooling.apis.journey
  (:require [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]))

  (defn add-journey [journey]
    (ac/add-journey journey)
    {:status 200})
