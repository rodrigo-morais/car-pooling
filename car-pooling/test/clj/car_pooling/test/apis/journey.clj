(ns car-pooling.test.apis.journey
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]
    [car-pooling.apis.journey :refer [add-journey drop-off-journey]]))

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

      (testing "drop-off-journey"
        (reset! db/*data* {:cars [] :journeys [journey-1 journey-2]})

        (testing "when the id is valid and the journey exists"
          (testing "returns 200 as status"
            (is (= (drop-off-journey "1") {:status 200})))

          (testing "calls `add-journey` from data.actions"
            (let [call-drop-off-journey (atom 0)]
              (with-redefs [ac/drop-off-journey (fn [id] (swap! call-drop-off-journey + 1) nil)]
              (drop-off-journey "2")
              (is (= @call-drop-off-journey 1))))))

        (testing "when the id is valid and the journey does not exists"
          (testing "returns 404 as status"
            (is (= (drop-off-journey "10") {:status 404}))))

          (testing "when id is invalid"
            (testing "returns 400 as status"
              (is (= (drop-off-journey "invalid") {:status 400 :body {}})))))

      (mount/stop #'car-pooling.data.core/*data*)))
