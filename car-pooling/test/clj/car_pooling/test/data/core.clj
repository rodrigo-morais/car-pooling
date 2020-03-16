(ns car-pooling.test.data.core
  (:require
    [clojure.test :refer :all]
    [mount.core :as mount]
    [car-pooling.data.core :as db]))

(deftest defstate
  (testing "core"
    (let [data {:cars []}]
      (->
        (mount/only #{#'car-pooling.data.core/*data*})
        (mount/start))
      (is (= @db/*data* data))
      (mount/stop #'car-pooling.data.core/*data*))))
