(ns car-pooling.test.apis.journey
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]
    [car-pooling.apis.journey :refer [add-journey]]))

(def journey-1
  {:id 1 :people 3})

(def journey-2
  {:id 2 :people 4})

(deftest defstate
  (testing "apis"
    (->
      (mount/only #{#'car-pooling.data.core/*data*})
      (mount/start))

    (testing "journey"
      (testing "add-journey"
        (testing "returns 200 as status"
          (is (= (add-journey journey-1) {:status 200})))

        (testing "calls `add-journey` from data.actions"
          (let [call-add-journey (atom 0)]
            (with-redefs [ac/add-journey (fn [journey] (swap! call-add-journey + 1) nil)]
            (add-journey journey-2)
            (is (= @call-add-journey 1))))))

        (testing "when adds a journey with a existent id"
          (testing "returns 400 as status"
            (is (= (add-journey journey-1) {:status 400 :body {}})))))

      (mount/stop #'car-pooling.data.core/*data*)))
