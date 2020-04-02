(ns car-pooling.test.data.actions
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]))

(deftest defstate
  (testing "actions"
    (->
      (mount/only #{#'car-pooling.data.core/*data*})
      (mount/start))

    (testing "load cars"
      (let [cars [{:id 1 :seats 2} {:id 2 :seats 4}]
            result (map (fn [car] (assoc car :seats-available (:seats car))) cars)]
        (ac/load-cars cars)
        (is (= (:cars @db/*data*) result))))

    (testing "journeys"
      (reset! db/*data* {:cars [] :journeys []})

      (testing "add journeys"
        (testing "when journeys is empty"
          (testing "adds the journey to the journeys"
            (let [journey   {:id 1 :people 3}
                  result    [(assoc journey :car nil)]]
              (ac/add-journey journey)
              (is (= (:journeys @db/*data*) result)))))

        (testing "when already exist journeys"
          (testing "does not add the jorney to te journeys"
            (let [journeys  (:journeys @db/*data*)
                  journey   {:id 2 :people 4}
                  result    (conj journeys (assoc journey :car nil))]
              (ac/add-journey journey)
              (is (= (:journeys @db/*data*) result))))))

      (testing "journey exists"
        (testing "when journey exists"
          (testing "returns true"
            (is (= (ac/journey-exist? 1) true))))

          (testing "when journey does not exist"
            (testing "returns false"
              (is (= (ac/journey-exist? 3) false)))))

        (testing "drop off journey"
          (let [result [{:id 2 :people 4 :car nil}]]
            (testing "when the journey exists"
              (testing "removes the journey"
                (ac/drop-off-journey 1)
                (is (= (:journeys @db/*data*) result))))

            (testing "when the journey does not exist"
              (testing "does not change the journeys"
                (ac/drop-off-journey 3)
                (is (= (:journeys @db/*data*) result))))))

        (testing "get journey car"
          (reset! db/*data* {:cars [{:id 1 :seats 2 :seats-available 2}] :journeys [{:id 1 :people 2 :car 1} {:id 2 :people 6 :car nil}]})
          (testing "when the journey has a valid car"
              (testing "returns the car id"
                (is (= (ac/get-journey-car 1) 1))))

          (testing "when the journey does not have a car"
            (testing "returns nil"
              (is (nil? (ac/get-journey-car 2)))))

          (testing "when the journey does not exist"
            (testing "returns nil"
              (is (nil? (ac/get-journey-car 5))))))

    (testing "watcher"
      (let [journey {:id 1 :people 3 :car nil}
            journeys [journey]
            car-id 1
            car {:id car-id :seats 3 :seats-available 3}
            cars [car]]
        (reset! db/*data* {:cars cars :journeys journeys})
      (testing  "connect car to journey"
          (testing "returns a new journey with a car"
            (ac/connect-car-to-journey journey)
            (is (= (:car (first (:journeys @db/*data*))) car-id))))
      (testing  "make car unvailable"
          (testing "returns a car unavailable"
            (ac/make-car-seats-unavailable car-id 3)
            (let [cars-by-id (first (filter (fn [_car] (= (:id _car) car-id)) (:cars @db/*data*)))
                  is-unavailable (zero? (:seats-available cars-by-id))]
              (is is-unavailable))))
      (testing  "make car available"
          (testing "returns a car available"
            (ac/make-car-seats-available car-id)
            (let [cars-by-id (first (filter (fn [_car] (= (:id _car) car-id)) (:cars @db/*data*)))
                  is-available (= (:seats-available cars-by-id) (:seats cars-by-id))]
              (is is-available)))))
      (testing "start journeys with avaible cars"
        (testing "when journeys have enough cars"
          (let [cars [{:id 1 :seats 2 :seats-available 2} {:id 2 :seats 4 :seats-available 4} {:id 3 :seats 6 :seats-available 6}]
                journeys [{:id 1 :people 2 :car nil} {:id 2 :people 6 :car nil}]]
            (reset! db/*data* {:cars cars :journeys journeys})
            (ac/start-journeys-with-avaliable-cars cars)
            (doseq [journey (:journeys @db/*data*)]
             (is (not (nil? (:car journey)))))))
        (testing "when journeys does not have enough available cars"
          (let [cars [{:id 1 :seats 2 :seats-available 2}]
                journeys [{:id 1 :people 2 :car nil} {:id 2 :people 6 :car nil}]]
            (reset! db/*data* {:cars cars :journeys journeys})
            (ac/start-journeys-with-avaliable-cars cars)
            (doseq [journey (:journeys @db/*data*)]
             (is (some #(nil? (:car %)) (:journeys @db/*data*))))))
        (testing "when a journey need more seats than cars can offer"
          (let [cars [{:id 1 :seats 2 :seats-available 2} {:id 2 :seats 4 :seats-available 4}]
                journeys [{:id 1 :people 2 :car nil} {:id 2 :people 6 :car nil}]]
            (reset! db/*data* {:cars cars :journeys journeys})
            (ac/start-journeys-with-avaliable-cars cars)
            (doseq [journey (:journeys @db/*data*)]
             (is (some #(nil? (:car %)) (:journeys @db/*data*))))))))

    (mount/stop #'car-pooling.data.core/*data*))))
