FROM clojure

RUN mkdir -p /car-pooling-challenge

COPY ./car-pooling-challenge/project.clj /car-pooling-challenge

WORKDIR /car-pooling-challenge

RUN lein deps

COPY /car-pooling-challenge/. /car-pooling-challenge

EXPOSE 9091

ENTRYPOINT ["lein", "run", "-p", "9091"]
