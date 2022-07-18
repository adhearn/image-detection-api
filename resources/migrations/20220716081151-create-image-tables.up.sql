CREATE TABLE images
(id SERIAL PRIMARY KEY,
 url TEXT,
 image TEXT,
 label TEXT NOT NULL
)

--;;

CREATE TABLE image_detections
(
  id serial PRIMARY KEY,
  image_id INTEGER,
  object_label TEXT NOT NULL,
  confidence FLOAT,
  detection_source TEXT,
  FOREIGN KEY (image_id) REFERENCES images (id)
)
