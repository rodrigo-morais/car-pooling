(ns car-pooling.test.data.actions
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]))

(deftest defstate
  (testing "actions"
    (testing "load cars"
      (let [cars [{:id 1 :seats 2} {:id 2 :seats 4}]]
        (->
          (mount/only #{#'car-pooling.data.core/*data*})
          (mount/start))
        (ac/load-cars cars)
        (is (= (:cars @db/*data*) cars))
        (mount/stop #'car-pooling.data.core/*data*)))

    (testing "journeys"
      (->
        (mount/only #{#'car-pooling.data.core/*data*})
        (mount/start))

      (testing "add journeys"
        (testing "when journeys is empty"
          (testing "returns the journeys with the new journey"
            (let [journey   {:id 1 :people 3}
                  result    [journey]]
              (ac/add-journey journey)
              (is (= (:journeys @db/*data*) result)))))

        (testing "when already exist journeys"
          (testing "returns the current journeys with the new journey"
            (let [journeys  (:journeys @db/*data*)
                  journey   {:id 2 :people 4}
                  result    (conj journeys journey)]
              (ac/add-journey journey)
              (is (= (:journeys @db/*data*) result))))))

      (testing "journey exists"
        (testing "when journey exists"
          (testing "returns true"
            (is (= (ac/journey-exist? 1) true))))

        (testing "when journey does not exist"
          (testing "returns false"
            (is (= (ac/journey-exist? 3) false)))))

      (mount/stop #'car-pooling.data.core/*data*))))
