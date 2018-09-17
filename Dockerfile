FROM maven:3.5.2-jdk-8-slim   
MAINTAINER David Esner <esnerda@gmail.com>

COPY . /code/

# set switch that enables correct JVM memory allocation in containers
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"
ENV MAVEN_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"

WORKDIR /code/
RUN mvn compile

ENTRYPOINT mvn -q exec:java -Dexec.args=/data  
