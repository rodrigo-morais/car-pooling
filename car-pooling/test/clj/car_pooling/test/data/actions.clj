(ns car-pooling.test.data.actions
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]))

(deftest defstate
  (testing "actions"
    (testing "load cars"
      (let [cars [{:id 1 :seats 2} {:id 2 :seats 4}]
            result (map (fn [car] (assoc car :available true)) cars)]
        (->
          (mount/only #{#'car-pooling.data.core/*data*})
          (mount/start))
        (ac/load-cars cars)
        (is (= (:cars @db/*data*) result))
        (mount/stop #'car-pooling.data.core/*data*)))

    (testing "journeys"
      (->
        (mount/only #{#'car-pooling.data.core/*data*})
        (mount/start))

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

      (mount/stop #'car-pooling.data.core/*data*))))
