(ns image-detection-api.models
  (:require
   [schema.core :as s]
   )
  (:gen-class))

(def sample-image
  {:id 1
   :label "test"
   :detections [{:detected-object "cat" :confidence 0.9 :source "Manual"}]})

(def ImageDetectionSchema
  {:detected-object s/Str
   :confidence s/Num
   :source s/Str})

(def ImageSchema
  {:id s/Int
   :label s/Str
   :detections [ImageDetectionSchema]})
