FROM debian:latest

RUN apt update && apt install ocl-icd-libopencl1

COPY ./build/native/nativeCompile/work-server /app/work-server

WORKDIR /app

RUN groupadd -r app && useradd -r -g app app
USER app

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["./work-server"]
