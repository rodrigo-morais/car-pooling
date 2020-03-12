FROM clojure:openjdk-14-alpine

RUN apk --no-cache add ca-certificates=20190108-r0 libc6-compat=1.1.19-r10

RUN mkdir -p /car-pooling-challenge

COPY ./car-pooling-challenge/project.clj /car-pooling-challenge

WORKDIR /car-pooling-challenge

RUN lein deps

COPY /car-pooling-challenge/. /car-pooling-challenge

EXPOSE 9091

ENTRYPOINT ["lein", "run", "-p", "9091"]
