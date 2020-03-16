(ns car-pooling.test.apis.journey
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]
    [car-pooling.apis.journey :refer [add-journey]]))

(def journey
  {:id 1 :people 3})

(deftest defstate
  (testing "apis"
    (->
      (mount/only #{#'car-pooling.data.core/*data*})
      (mount/start))

    (testing "journey"
      (testing "add-journey"
        (testing "returns 200 as status and the body with the journey"
          (is (= (add-journey journey) {:status 200})))))
        (testing "calls `add-journey` from data.actions"
          (let [call-add-journey (atom 0)]
            (with-redefs [ac/add-journey (fn [journey] (swap! call-add-journey + 1) nil)]
            (add-journey journey)
            (is (= @call-add-journey 1)))))

      (mount/stop #'car-pooling.data.core/*data*)))
