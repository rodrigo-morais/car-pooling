# Car Pooling

## RUN

```
docker image build -f Docker.devt car-pooling .
docker container run -it -p 9091:9091 --name cabify car-pooling:latest
```

## TEST

```
docker container run -it -p 9091:9091 --name cabify car-pooling:latest
docker exec cabify lein test
```
