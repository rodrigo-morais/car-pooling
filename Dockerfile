FROM clojure:openjdk-14-alpine

RUN mkdir -p /car-pooling-challenge
WORKDIR /car-pooling-challenge

COPY /car-pooling/project.clj ./
RUN lein deps

COPY /car-pooling/. ./

RUN lein uberjar

RUN apk --no-cache add ca-certificates=20190108-r0 libc6-compat

EXPOSE 9091
ENV PORT=9091

ENTRYPOINT ["java", "-jar", "/car-pooling-challenge/target/uberjar/car-pooling.jar"]
