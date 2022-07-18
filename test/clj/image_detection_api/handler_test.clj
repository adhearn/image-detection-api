(ns image-detection-api.handler-test
  (:require
    [clojure.test :refer :all]
    [ring.mock.request :refer :all]
    [image-detection-api.handler :refer :all]
    [image-detection-api.middleware.formats :as formats]
    [muuntaja.core :as m]
    [mount.core :as mount]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'image-detection-api.config/env
                 #'image-detection-api.handler/app-routes)
    (f)))

(deftest test-app
  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response)))))
  (testing "images"

    (testing "success"
      (let [response ((app) (-> (request :get "/images")))]
        (is (= 200 (:status response)))))))
