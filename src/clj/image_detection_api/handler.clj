(ns image-detection-api.handler
  (:require
   [image-detection-api.errors :refer [error-response]]
   [image-detection-api.middleware :as middleware]
   [image-detection-api.routes.images :refer [image-routes]]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring :as ring]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.webjars :refer [wrap-webjars]]
   [image-detection-api.env :refer [defaults]]
   [mount.core :as mount]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
     [(image-routes)])
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path   "/swagger/ui"
         :url    "/swagger/swagger.json"
         :config {:validator-url nil}})
      (ring/create-resource-handler
        {:path "/"})
      (wrap-content-type
        (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found
         (constantly (error-response {:status 404, :message "Not found"}))
         :method-not-allowed
         (constantly (error-response {:status 405, :message "Not allowed"}))
         :not-acceptable
         (constantly (error-response {:status 406, :message "Not acceptable"}))}))))

(defn app []
  (middleware/wrap-base #'app-routes))
