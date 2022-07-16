(ns image-detection-api.handlers.images
  (:require
   [image-detection-api.nrepl :as nrepl]
   [clojure.tools.logging :as log]
   [image-detection-api.db.core :as db]
   [image-detection-api.models :as models]
   )
  (:gen-class))

(defn error-response
  [status-code msg]
  {:status status-code
   :body {:error msg}})

(defn success-response
  [data]
  {:status 200
   :body {:data data}})

(defn detect-objects
  [image]
  (assoc image :detections []))

(defn save-image!
  [image]
  (let [detections (:detections image)
        image-id (db/insert-image! image)]
    (println image-id)
    (doseq [detection (:detections image)]
      (db/insert-detection! (assoc detection :image_id (:id image))))
    image))


(defn get-all-images
  "Returns all images, optionally filtering to just those that match specified labels"
  [{{{:keys [objects]} :query} :parameters}]
  {:status 200
   ; We wrap the response with {data: ...} to avoid a security issue with JS handling of JSON: http://haacked.com/archive/2008/11/20/anatomy-of-a-subtle-json-vulnerability.aspx/
   :body {:data [models/sample-image]}})

(defn post-image
  [{{{:keys [image url label detectObjects]} :body} :parameters}]
  (println image url)
  (cond
    (and image url) (error-response 400 "cannot provide both image contents and image url")
    (not (or image url)) (error-response 400 "must provide either image contents or image url")
    :else
    (let [label (if label label (str (gensym)))
          image {:image image
                 :url  url
                 :label label}]
      (-> image
          (detect-objects)
          (save-image!)
          (success-response)))))
