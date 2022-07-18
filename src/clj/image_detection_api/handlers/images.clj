(ns image-detection-api.handlers.images
  (:require
   [clojure.string :as s]
   [clojure.tools.logging :as log]
   [failjure.core :as fail]
   [image-detection-api.db.core :as db]
   [image-detection-api.detection :as detection]
   [image-detection-api.errors :refer [error-response]]
   [image-detection-api.models :as models])
  (:gen-class))

(defn success-response
  [data]
  {:status 200
   ;; We wrap the response with {data: ...} to avoid a security issue with JS handling of JSON: http://haacked.com/archive/2008/11/20/anatomy-of-a-subtle-json-vulnerability.aspx/
   :body {:data data}})

(defn make-detection-response
  [label confidence source]
  {:detected-object label
   :confidence confidence
   :source source})

(defn save-image!
  [image]
  (let [detections (:detections image)
        {:keys [:id]} (db/insert-image! image)]
    (doseq [detection (:detections image)]
      (db/insert-detection! (assoc detection :image-id id)))
    (assoc image :id id)))

(defn partition-by-image
  [images-by-id {:keys [id image url label detection_label confidence detection_source] :as flat-image}]
  (let [detection (make-detection-response detection_label confidence detection_source)]
    (if (contains? images-by-id id)
      (assoc-in images-by-id [id :detections] (conj (get-in images-by-id [id :detections]) detection))
      (assoc images-by-id id {:id id
                               :image image
                               :url url
                               :label label
                               :detections (if detection_label [detection] [])}))))

(defn get-image
  [{{{:keys [imageId]} :path} :parameters}]
  (let [image (get (reduce partition-by-image {} (db/get-image-by-id {:id imageId})) imageId)]
    (if (nil? image)
      (error-response {:status 404 :message (str "no image found for id: " imageId)})
      (success-response image))))

(defn get-all-images
  "Returns all images, optionally filtering to just those that match specified labels"
  [{{{:keys [objects]} :query} :parameters}]
  (let [images-flat (if objects
                      (db/get-images-with-detection-labels {:filter_labels (s/split objects #",")})
                      (db/get-all-images))]
    (success-response (vals (reduce partition-by-image {} images-flat)))))

(defn post-image
  [{{{:keys [image url label detectObjects]} :body} :parameters}]
  (cond
    (and image url) (error-response {:status 400 :message "cannot provide both image contents and image url"})
    (not (or image url)) (error-response {:status 400 :message "must provide either image contents or image url"})
    :else
    (let [label (if label label (str (gensym)))
          image {:image image
                 :url  url
                 :label label}]
      (let [result (fail/ok-> image
                       (detection/detect-objects)
                       (save-image!)
                       (success-response))]
        (if (fail/failed? result)
            (error-response {:status 400 :message (fail/message result)})
            result)))))
