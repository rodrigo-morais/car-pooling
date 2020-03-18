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

  (defn drop-off-journey [id]
    (let [_id (read-string id)
          is-number? (number? _id)]
      (if is-number?
        (if (ac/journey-exist? _id)
          (do
            (ac/drop-off-journey _id)
            {:status 200})
          {:status 404})
        {:status 400 :body {}})))
