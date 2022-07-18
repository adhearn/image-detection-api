(ns image-detection-api.errors
  (:require
   [cheshire.core :as cheshire]))

(defn error-response
  [error-details]
  {:status  (:status error-details)
   :headers {"Content-Type" "application/json"}
   :body (cheshire/generate-string {:error (:message error-details)})})
