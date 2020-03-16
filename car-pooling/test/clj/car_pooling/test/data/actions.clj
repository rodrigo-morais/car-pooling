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
        (mount/stop #'car-pooling.data.core/*data*)))))
