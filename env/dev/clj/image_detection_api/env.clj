(ns image-detection-api.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [image-detection-api.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[image-detection-api started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[image-detection-api has shut down successfully]=-"))
   :middleware wrap-dev})
