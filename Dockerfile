FROM eclipse-temurin:21 as jdk

COPY ./build/libs/work-server.jar /work-server.jar

RUN jar -xvf work-server.jar && jlink --add-modules $(jdeps --recursive --multi-release 21 --ignore-missing-deps --print-module-deps -cp 'BOOT-INF/lib/*' work-server.jar) --output /java

FROM ubuntu

LABEL org.opencontainers.image.source https://github.com/attocash/work-server

ENV JAVA_HOME=/java
ENV PATH "${JAVA_HOME}/bin:${PATH}"

RUN adduser --disabled-password --gecos "" atto
USER atto

RUN apt-get update && apt-get install -y ocl-icd-libopencl1

COPY ./build/libs/work-server.jar /home/atto/work-server.jar

COPY --from=jdk /java /java

ENTRYPOINT ["java","-XX:+UseZGC","-jar","/home/atto/work-server.jar"]
