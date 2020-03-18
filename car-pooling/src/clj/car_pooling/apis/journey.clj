(ns car-pooling.apis.journey
  (:require [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]))

  (defn add-journey [journey]
    (let [id (get journey :id)]
      (if (ac/journey-exist? id)
        {:status 400 :body {}}
        (do
          (ac/add-journey journey)
          {:status 200}))))
