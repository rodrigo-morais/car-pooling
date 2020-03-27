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
      (let [call-load-cars (atom 0)]
        (with-redefs [ac/load-cars (fn [cars] (swap! call-load-cars + 1) nil)
                      ac/cars-exist? (fn [cars] false)]

        (testing "load-cars"
          (testing "returns 200 as status and the body with cars"
            (is (= (load-cars cars) {:status 200})))
          (testing "calls `load-cars` from data.actions"
            (reset! call-load-cars 0)
            (load-cars cars)
            (is (= @call-load-cars 1)))

          (testing "when data has unavailable cars and it is repeated in the cars to be loaded"
            (testing "returns 400 as status"
              (with-redefs [ac/cars-exist? (fn [cars] true)]
                (reset! db/*data* {:cars [{:id 1 :seats 2 :available true} {:id 2 :seats 4 :available false}]})
                (is (= (load-cars cars) {:status 400 :body {}})))))))))

      (mount/stop #'car-pooling.data.core/*data*)))
