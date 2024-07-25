FROM debian:latest

RUN apt update && apt install ocl-icd-libopencl1 clinfo

COPY ./build/native/nativeCompile/work-server /app/work-server

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["./work-server"]
