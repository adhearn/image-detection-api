CREATE TABLE images
(id INTEGER PRIMARY KEY AUTOINCREMENT,
 url VARCHAR,
 image BINARY LARGE OBJECT,
 label VARCHAR NOT NULL
);

CREATE TABLE image_detections
(
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  image_id INTEGER,
  object_label VARCHAR NOT NULL,
  confidence FLOAT,
  detection_source VARCHAR,
  FOREIGN KEY (image_id) REFERENCES images (id)
)
