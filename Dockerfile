FROM docker.io/library/eclipse-temurin:25 AS jdk
COPY ./build/libs/work-server.jar /work-server.jar
RUN jar -xvf work-server.jar && jlink --add-modules $(jdeps --recursive --multi-release 25 --ignore-missing-deps --print-module-deps -cp 'BOOT-INF/lib/*' work-server.jar) --output /java

FROM docker.io/library/ubuntu:22.04

ARG APPLICATION_VERSION

LABEL org.opencontainers.image.title="atto-work-server" \
      org.opencontainers.image.description="Atto work server" \
      org.opencontainers.image.url="https://atto.cash" \
      org.opencontainers.image.source="https://github.com/attocash/work-server" \
      org.opencontainers.image.version="${APPLICATION_VERSION}"

ENV APPLICATION_VERSION=${APPLICATION_VERSION}
ENV JAVA_HOME=/java
ENV PATH "${JAVA_HOME}/bin:${PATH}"

RUN useradd -m -s /bin/bash app

USER app

COPY ./build/libs/work-server.jar /home/atto/work-server.jar
COPY --from=jdk /java /java

ENTRYPOINT ["java","-XX:+UseZGC","-jar","/home/atto/work-server.jar"]
