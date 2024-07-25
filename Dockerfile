FROM alpine:latest

RUN apk add --no-cache \
    opencl-headers \
    ocl-icd \
    ocl-icd-dev

COPY ./build/native/nativeCompile/work-server /app/work-server

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["./work-server"]
