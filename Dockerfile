FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven

WORKDIR /framework

COPY . /framework

VOLUME ["/framework/contractstudy", "/framework/datasetScripts"]