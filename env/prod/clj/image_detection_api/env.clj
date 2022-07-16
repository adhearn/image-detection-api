(ns image-detection-api.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[image-detection-api started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[image-detection-api has shut down successfully]=-"))
   :middleware identity})
