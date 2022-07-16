(ns image-detection-api.handlers.images
  (:require
   [image-detection-api.nrepl :as nrepl]
   [clojure.tools.logging :as log]
   [image-detection-api.models :as models]
   )
  (:gen-class))

(defn get-all-images
  "Returns all images, optionally filtering to just those that match specified labels"
  [{{{:keys [objects]} :query} :parameters}]
  {:status 200
   :body {:data models/sample-image}}
  )
