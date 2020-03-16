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
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]))

(s/def ::id number?)
(s/def ::seats number?)
(s/def ::car (s/keys  :req-un [::id ::seats]))
(s/def ::cars (s/coll-of ::car))
(s/def ::fleet (s/keys :req-un [::cars]))

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
              :parameters {:body ::fleet}
              :responses {200 {:body {}}
                          400 {:body {}}}
              :handler (fn [{{{:keys [cars]} :body} :parameters}]
                (load-cars cars))}}]])
