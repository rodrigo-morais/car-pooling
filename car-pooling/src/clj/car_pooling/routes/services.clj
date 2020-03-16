(ns car-pooling.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [clojure.spec.alpha :as s]
    [car-pooling.middleware.formats :as formats]
    [car-pooling.middleware.exception :as exception]
    [car-pooling.apis.cars :refer [load-cars]]
    [car-pooling.apis.journey :refer [add-journey]]
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]))

(s/def ::id number?)
(s/def ::seats number?)
(s/def ::people number?)

(s/def ::car (s/keys  :req-un [::id ::seats]))
(s/def ::cars (s/coll-of ::car))

(s/def ::journey (s/keys  :req-un [::id ::people]))

(defn service-routes []
  [""
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/swagger.json"
              :config {:validator-url nil}})}]]

   ["/status"
    {:get (constantly (ok {}))}]
   
    ["/cars"
      {:put {:summary "Load the list of available cars"
              :parameters {:body ::cars}
              :responses {200 {:body nil}
                          400 {:body {}}}
              :handler (fn [{{:keys [body]} :parameters}]
                (load-cars body))}}]

    ["/journey"
      {:post {:summary "Add a journey"
              :parameters {:body ::journey}
              :responses {200 {:body nil}
                          400 {:body {}}}
              :handler (fn [{{:keys [body]} :parameters}]
                (add-journey body))}}]])
