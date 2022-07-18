# image-detection-api

## Running

To run the service via docker compose:

``` shell
# Only need to rebuild the jar if something has changed. To make things simple, I've included the jar in the repo.
# An alternative would be a docker image that knew how to create its own jar.
# lein uberjar
docker compose build
docker compose up -d
```

In additiont to the service itself, docker compose will spin up a postgres database, using host port 54321. The image detection API is exposed on port 3000. The Swagger UI is available at http://localhost:3000/swagger/ui.

## API Details
In addition to a ping endpoint, there are 3 endpoints exposed by the service:

* `GET /images(?objects=label1,label2,...)` returns all images, optionally filtering by the given labels. Filtering acts as a union, so if you provide the objects string "dog,cat" you will get all images containing dogs and all images containing cats, not all images that contain both a dog and a cat.

* `GET /images/:imageID` returns the image and object details for the specified image.

* `POST /images` allows submitting a new image to save, with the option to detect objects in the image. The expected payload is:

``` json
{
  "image": <base 64 string>,
  "url": "http://url.to.some/image.jpg",
  "label": "optional label for the image",
  "detectObjects": true
}
```

Exactly one of `images` and `url` must be provided. Image should be a base64 encoded string of the image contents. `url` should be a url pointing to the image. `label` is an optional text label for the image. `detectObjects` is a boolean that tells the service whether or not to send the image for object detection.

### Response Format
All endpoints can return one or more images, and images can have 0 or more detections. A single detection looks like:

``` json
{
  "detected-object": "cat",
  "confidence": 87.2,
  "source": "Imagga"
}
```

A single image looks like:


``` json
{
  "image": <base64 string or "">,
  "url": "http://url.to.some/image.jpg",
  "label": "optional label for iamge",
  "detections": [<detection objects as described above>]
}
```

All responses are wrapped in {"data": ...} to avoid an old Javascript vulnerability: http://haacked.com/archive/2008/11/20/anatomy-of-a-subtle-json-vulnerability.aspx/. This has the additional advantage of being able to respond with a value and an error message (for example, to handle a degraded level of functionality).

## Detection Implementation
Currently, Imagga is the only supported detection backend. To use Imagga, an API key and secret must be provided. Those values can either be placed directly into the docker-compose.yml file, or will be read from environment variables from the process that launches docker. If provided as environment variables, they must use the variables `IMAGGA__API_KEY` and `IMAGGA__API_SECRET` - note the double underscore!i
