-- :name insert-image! :<!
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
