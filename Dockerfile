FROM gcr.io/distroless/static:nonroot

COPY ./build/native/nativeCompile/work-server /app/work-server

WORKDIR /app

USER nonroot:nonroot

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["./work-server"]
