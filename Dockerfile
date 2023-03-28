FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven

VOLUME ["/contractstudy", "/datasetScripts"]
WORKDIR /contractstudy

CMD ["mvn", "clean", "install"]