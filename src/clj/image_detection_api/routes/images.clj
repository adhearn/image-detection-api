(ns image-detection-api.routes.images
  (:require
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.coercion :as coercion]
   [reitit.coercion.schema :as schema-coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [schema.core :as s]
   [image-detection-api.middleware.formats :as formats]
   [image-detection-api.models :as models]
   [image-detection-api.handlers.images :as image-handlers]
   [ring.util.http-response :refer :all]
   [clojure.java.io :as io]))



(defn image-routes []
  [""
   {:coercion schema-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["/swagger" {:no-doc true
                :swagger {:info {:title "my-api"
                                 :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
            {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/ping"
    {:get (constantly (ok {:message "pong"}))}]

   ["/images"
    {:get {:summary "get all images (optionally filtering to specific detected objects)"
           :responses {200 {:body {:data models/ImageSchema}}}
           :handler image-handlers/get-all-images}}]

   ])
