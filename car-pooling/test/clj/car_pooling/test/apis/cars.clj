(ns car-pooling.test.apis.cars
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]
    [car-pooling.apis.cars :refer [load-cars]]))

(def cars
  [cars [{:id 1 :seats 2} {:id 2 :seats 4}]])

(deftest defstate
  (testing "apis"
    (->
      (mount/only #{#'car-pooling.data.core/*data*})
      (mount/start))

    (testing "cars"
      (testing "load-cars"
        (testing "returns 200 as status and the body with cars"
          (is (= (load-cars cars) {:status 200 :body {:cars cars}})))))
        (testing "calls `load-cars` from data.actions"
          (let [call-load-cars (atom 0)]
            (with-redefs [ac/load-cars (fn [cars] (swap! call-load-cars + 1) nil)]
            (load-cars cars)
            (is (= @call-load-cars 1)))))

      (mount/stop #'car-pooling.data.core/*data*)))
