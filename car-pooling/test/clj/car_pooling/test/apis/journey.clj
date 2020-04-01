(ns car-pooling.test.apis.journey
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]
    [car-pooling.data.actions :as ac]
    [car-pooling.apis.journey :refer [add-journey drop-off-journey get-journey-car]]))

(def journey-1
  {:id 1 :people 3 :car 1})

(def journey-2
  {:id 2 :people 4 :car nil})

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

      (testing "get-journey-car"
        (reset! db/*data* {:cars [] :journeys [journey-1 journey-2]})

        (testing "when the id is valid and the journey has a valid car"
          (testing "returns 200 as status"
            (is (= (get-journey-car "1") {:status 200 :body {:id 1}})))

          (testing "calls `get-journey-car` from data.actions"
            (let [call-get-journey-car (atom 0)]
              (with-redefs [ac/get-journey-car (fn [id] (swap! call-get-journey-car + 1) nil)]
              (get-journey-car "2")
              (is (= @call-get-journey-car 1))))))

        (testing "when the id is valid and the journey is waiting a car"
          (testing "returns 204 as status"
            (is (= (get-journey-car "2") {:status 204}))))

        (testing "when the id is valid and the journey does not exists"
          (testing "returns 404 as status"
            (is (= (get-journey-car "10") {:status 404}))))

          (testing "when id is invalid"
            (testing "returns 400 as status"
              (is (= (get-journey-car "invalid") {:status 400 :body {}})))))

      (mount/stop #'car-pooling.data.core/*data*)))
