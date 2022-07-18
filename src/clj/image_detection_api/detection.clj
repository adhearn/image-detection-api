(ns image-detection-api.detection
  (:require
   [cheshire.core :as cheshire]
   [clj-http.client :as client]
   [clojure.tools.logging :as log]
   [failjure.core :as fail]
   [image-detection-api.config :refer [env]])
  (:gen-class))

(def imagga-tag-url "https://api.imagga.com/v2/tags")
(def imagga-min-confidence 30)
(def imagga-source-name "Imagga")

(defn imagga-creds
  "Returns credentials required to make Imagga API requests"
  []
  (let [key (get-in env [:imagga :api-key])
        secret (get-in env [:imagga :api-secret])]
    {:key key
     :secret secret}))

(defn imagga-tag->detection
  "Parse a single Imagga tag result into a detection map"
  [{:keys [confidence tag]}]
  {:detected-object (:en tag)
   :confidence confidence
   :source imagga-source-name})

(defn imagga-response->detections
  "Parse an Imagga /tag response into a sequence of detection maps"
  [imagga-response]
  (let [tags (get-in imagga-response [:body :result :tags])]
    (->> tags
         (map imagga-tag->detection)
         (filter #(>= (:confidence %) imagga-min-confidence)))))  ;; This also can (and probably should) be done via the Imagga API

(defn imagga-detect-url
  "Use the Imagga API to detect images in an image at a particular URL"
  [image-url]
  (let [url (str imagga-tag-url "?image_url=" image-url)
        {:keys [key secret] :as creds} (imagga-creds)
        args {:basic-auth [key, secret]
              :as :json
              :throw-exceptions false}]
    (client/get url args)))

(defn imagga-detect-base64
  "Use the Imagga API to detect objects in an image encoded as a base64 string"
  [image-contents]
  (let [{:keys [key secret] :as creds} (imagga-creds)
        args {:basic-auth [key, secret]
              :form-params {:image_base64 image-contents}
              :as :json
              :throw-exceptions false}]
    (client/post imagga-tag-url args)))

(defn imagga-detect
  "Detect objects in a image via the Imagga API and return a sequence of detection maps"
  [image]
  (let [image-url (:url image)
        image-contents (:image image)
        resp (if image-url
               (imagga-detect-url image-url)
               (imagga-detect-base64 image-contents))]
    (if (= (:status resp) 200)
      (imagga-response->detections resp)
      (let [error-body (cheshire/parse-string (:body resp))
            resp-msg (get-in error-body ["status" "text"])
            error-msg (str "request to Imagga failed with status code " (:status resp) ": " resp-msg)]
        (fail/fail error-msg)))))


(defn detect-objects
  "Detect objects in a given image map. Currently, Imagga is supported detection provider."
  [image]
  (let [result (imagga-detect image)]
    (if (fail/failed? result)
      result
      (assoc image :detections result))))
