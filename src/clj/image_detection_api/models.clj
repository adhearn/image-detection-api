(ns image-detection-api.models
  (:require
   [schema.core :as s]
   )
  (:gen-class))

;; Models representing the key objects in our domain.
;; Following Clojure custom, we use Maps for simplicity
;; (https://cemerick.com/blog/2011/07/05/flowchart-for-choosing-the-right-clojure-type-definition-form.html)

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
   (s/optional-key :url) s/Str
   (s/optional-key :image) s/Str
   :label s/Str
   :detections [ImageDetectionSchema]})
