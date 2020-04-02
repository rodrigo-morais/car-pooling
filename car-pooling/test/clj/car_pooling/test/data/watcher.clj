(ns car-pooling.test.data.watcher
  (:require
    [clojure.test :refer :all]
    [car-pooling.data.actions :as ac]
    [car-pooling.data.watcher :as wc]))

(def _key {})

(def _atom nil)

(def call-start-journeys-with-avaliable-cars-count (atom 0))
(def call-start-journeys-with-avaliable-cars-param (atom nil))
(defn- start-journeys-with-avaliable-cars [cars]
  (swap! call-start-journeys-with-avaliable-cars-count + 1)
  (reset! call-start-journeys-with-avaliable-cars-param cars)
  nil)

(def call-connect-car-to-journey-count (atom 0))
(def call-connect-car-to-journey-param (atom nil))
(defn- connect-car-to-journey [journey]
  (swap! call-connect-car-to-journey-count + 1)
  (reset! call-connect-car-to-journey-param journey)
  nil)

(def call-make-car-seats-available-count (atom 0))
(def call-make-car-seats-available-param (atom nil))
(defn- make-car-seats-available [car]
  (swap! call-make-car-seats-available-count + 1)
  (reset! call-make-car-seats-available-param car)
  nil)

(def call-make-car-seats-unavailable-count (atom 0))
(def call-make-car-seats-unavailable-param (atom nil))
(defn- make-car-seats-unavailable [car seats]
  (swap! call-make-car-seats-unavailable-count + 1)
  (reset! call-make-car-seats-unavailable-param {:car-id car :seats seats})
  nil)

(deftest defstate
  (testing "watcher"
    (with-redefs [ac/start-journeys-with-avaliable-cars start-journeys-with-avaliable-cars
                  ac/connect-car-to-journey connect-car-to-journey
                  ac/make-car-seats-available make-car-seats-available
                  ac/make-car-seats-unavailable make-car-seats-unavailable]

      (testing "made new cars available"
        (let [old-state {:cars [] :journeys []}
              new-state {:cars [{:id 1 :seats 2 :seats-available 2}] :journeys []}
              param #{{:id 1 :seats 2 :seats-available 2}}]
        (testing "calls action start-journeys-with-avaliable-cars from data.actions"
            (wc/watch-changes _key _atom old-state new-state)
            (is (= @call-start-journeys-with-avaliable-cars-count 1))
            (is (= @call-start-journeys-with-avaliable-cars-param param)))))

      (testing "made new journey"
        (let [old-state {:cars [{:id 1 :seats 3 :seats-available 3}] :journeys []}
              new-state {:cars [{:id 1 :seats 3 :seats-available 3}] :journeys [{:id 1 :people 3}]}
              param {:id 1 :people 3}]
        (testing "calls action connect-car-to-journey from data.actions"
            (wc/watch-changes _key _atom old-state new-state)
            (is (= @call-connect-car-to-journey-count 1))
            (is (= @call-connect-car-to-journey-param param)))))

      (testing "make car available"
        (let [old-state {:cars [{:id 1 :seats 3 :seats-available 3}] :journeys [{:id 1 :people 3 :car 1}]}
              new-state {:cars [{:id 1 :seats 3 :seats-available 3}] :journeys []}
              param 1]
        (testing "calls action make-car-seats-available from data.actions"
            (wc/watch-changes _key _atom old-state new-state)
            (is (= @call-make-car-seats-available-count 1))
            (is (= @call-make-car-seats-available-param param)))))

      (testing "make car uavailable"
        (let [old-state {:cars [{:id 1 :seats 3 :seats-available 3}] :journeys [{:id 1 :people 3 :car nil}]}
              new-state {:cars [{:id 1 :seats 3 :seats-available 3}] :journeys [{:id 1 :people 3 :car 1}]}
              param {:car-id 1 :seats 3}]
        (testing "calls action make-car-seats-unavailable from data.actions"
            (wc/watch-changes _key _atom old-state new-state)
            (is (= @call-make-car-seats-unavailable-count 1))
            (is (= @call-make-car-seats-unavailable-param param))))))))
