-- :name insert-image! :<! :1
-- :doc inserts an image record and returns it
INSERT INTO images
(url, image, label)
VALUES (:url, :image, :label)
RETURNING id;

-- :name insert-detection! :!
-- :doc insert an image detection record and returns it
INSERT INTO image_detections
(image_id, object_label, confidence, detection_source)
VALUES (:image_id, :object_label, :confidence, :detection_source);

-- :name get-image-by-id :?
-- :doc retrieve a single image and all its associated detections
SELECT i.id AS id, i.image AS image, i.url as url, i.label AS label, dets.object_label AS detection_label, dets.confidence AS confidence, dets.detection_source AS detection_source
FROM images i
LEFT OUTER JOIN image_detections dets ON dets.image_id = i.id
WHERE i.id = :id

-- :name get-all-images :?
-- :doc retrieve all images and their associated detections
SELECT i.id AS id, i.image AS image, i.url as url, i.label AS label, dets.object_label AS detection_label, dets.confidence AS confidence, dets.detection_source AS detection_source
FROM images i
LEFT OUTER JOIN image_detections dets ON dets.image_id = i.id

-- :name get-images-images-with-detection-labels :?
-- :doc retrieve images and their associated detections with specific object detection labels
SELECT i.id AS id, i.image AS image, i.url as url, i.label AS label, dets.object_label AS detection_label, dets.confidence AS confidence, dets.detection_source AS detection_source
FROM images i
LEFT OUTER JOIN image_detections dets ON dets.image_id = i.id
WHERE i.id IN (
  SELECT DISTINCT image_id
  FROM image_detections
  WHERE detection_label IN :filter_labels
)
