(ns car-pooling.data.watcher
  (:require
    [car-pooling.data.actions :as ac]))

(defn watch-changes [key atom old-state new-state]
  (let [old-cars              (:cars old-state)
        old-journeys          (:journeys old-state)
        new-cars              (:cars new-state)
        new-journeys          (:journeys new-state)
        made-cars-available   (clojure.set/difference (set new-cars) (set old-cars))
        made-cars-available?  (> (count made-cars-available) 0)
        added-journey?        (> (count new-journeys) (count old-journeys))
        removed-journey       (clojure.set/difference (set old-journeys) (set new-journeys))
        removed-journey?      (> (count old-journeys) (count new-journeys))
        diff-journey          (clojure.set/difference (set new-journeys) (set old-journeys))
        connected-journey?    (> (count diff-journey) 0)]
    (cond
      made-cars-available? (ac/start-journeys-with-avaliable-cars made-cars-available)
      added-journey? (ac/connect-car-to-journey (first diff-journey))
      removed-journey? (ac/make-car-available (:car (first removed-journey)))
      connected-journey? (ac/make-car-unavailable (:car (first diff-journey))))))
