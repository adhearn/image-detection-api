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
   :url "https://upload.wikimedia.org/wikipedia/commons/d/dd/Micrathene_whitneyi_29APR12_Madera_Canyon_AZ.jpg"
   :label "test"
   :detections [{:detected-object "cat" :confidence 0.9 :source "Manual"}]})

(def ImageDetectionSchema
  {:detected-object s/Str
   :confidence s/Num
   :source s/Str})

(def ImageSchema
  {:id s/Int
   (s/optional-key :url) (s/maybe s/Str)
   (s/optional-key :image) (s/maybe s/Str)
   :label s/Str
   :detections [ImageDetectionSchema]})
