# Atto Work Server

Atto Work Server is the proof-of-work generation service for the **Atto** cryptocurrency. It exposes a small HTTP API that accepts a network, timestamp, and work target, then returns valid `AttoWork`.

This repository is a **Kotlin + Spring Boot (WebFlux)** application. It can run with CPU workers for testing, or OpenCL workers for GPU-backed production use.

[Website](https://atto.cash/) | [Docs](https://atto.cash/docs/integration) | [Commons](https://github.com/attocash/commons)

## Table of contents

- [What this service does](#what-this-service-does)
- [Quick start](#quick-start)
- [Configuration](#configuration)
- [Ports](#ports)
- [API](#api)
- [Code map](#code-map)
- [Docker images](#docker-images)
- [Troubleshooting](#troubleshooting)

## What this service does

At a high level, this service:

1. Accepts work requests through `POST /works`.
2. Rejects requests whose timestamp is more than one minute in the future.
3. Delegates work generation to a pool of Atto workers.
4. Returns generated work that can be validated against the requested network, timestamp, and target.

Worker mode is selected by Spring profile:

- `cpu`: uses a CPU worker. This is mainly for tests and local checks.
- `rocm`, `cuda`, `intel`: use OpenCL workers through the matching container image/runtime.

Any non-`cpu` profile uses OpenCL workers.

## Quick start

### Prerequisites

- **Java 25** (the Gradle toolchain is pinned to 24 in `build.gradle.kts`)
- A local OpenCL runtime if you want GPU-backed execution outside containers
- Podman or Docker if you want to run a published image

### Run tests and build

```sh
./gradlew build
```

### Run locally with CPU workers

```sh
SPRING_PROFILES_ACTIVE=cpu,json ./gradlew bootRun
```

Swagger UI is available at:

- `http://localhost:8080/`

### Run a GPU image

ROCm example:

```sh
podman run -it --rm --privileged --device /dev/dri -p 8080:8080 ghcr.io/attocash/work-server:rocm
```

CUDA example:

```sh
podman run -it --rm --gpus all -p 8080:8080 ghcr.io/attocash/work-server:cuda
```

Intel example:

```sh
podman run -it --rm --device /dev/dri -p 8080:8080 ghcr.io/attocash/work-server:intel
```

## Configuration

The main configuration namespace is `atto.work.*`.

Available environment variables:

- `ATTO_WORK_QUEUE_SIZE` (default `4`): number of OpenCL workers to create.
- `ATTO_WORK_DEVICE` (default `0`): OpenCL device index.

Spring profiles:

- `cpu,json`
- `rocm,json`
- `cuda,json`
- `intel,json`

The `json` profile enables structured JSON logging.

## Ports

From `application.yaml`:

- **8080**: public REST API (Spring WebFlux)
- **8081**: management/metrics (Actuator + Prometheus)

Swagger UI is configured at:

- `:8080/` (see `springdoc.swagger-ui.path: /`)

Management endpoints are exposed under the management port:

- `:8081/health`
- `:8081/metrics`
- `:8081/prometheus`

## API

### Generate work

`POST /works`

Request:

```json
{
  "network": "LOCAL",
  "timestamp": 1778630400000,
  "target": "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"
}
```

Response:

```json
{
  "work": "0000000000000000"
}
```

`timestamp` is Unix time in milliseconds. The exact target and work formats follow the Atto commons serializers.

## Code map

Common entry points:

- `src/main/kotlin/cash/atto/work/Application.kt` - Spring Boot entry point
- `src/main/kotlin/cash/atto/work/WorkController.kt` - HTTP API
- `src/main/kotlin/cash/atto/work/Worker.kt` - worker pool and request delegation
- `src/main/kotlin/cash/atto/work/ApplicationConfiguration.kt` - CPU/OpenCL worker selection
- `src/main/kotlin/cash/atto/work/ApplicationProperties.kt` - `atto.work.*` configuration

Tests:

- `src/test/resources/features/request.feature` - Cucumber scenario
- `src/test/kotlin/cash/atto/work/WorkStepDefinition.kt` - Cucumber step definitions

## Docker images

This repo includes a base image plus distribution images:

- `Dockerfile`: base application image
- `Dockerfile.cpu`: CPU profile
- `Dockerfile.rocm`: AMD ROCm/OpenCL profile
- `Dockerfile.cuda`: NVIDIA CUDA/OpenCL profile
- `Dockerfile.intel`: Intel OpenCL profile

Published tags include:

- `ghcr.io/attocash/work-server:cpu`
- `ghcr.io/attocash/work-server:rocm`
- `ghcr.io/attocash/work-server:cuda`
- `ghcr.io/attocash/work-server:intel`

Release images are also tagged with versioned suffixes such as:

- `ghcr.io/attocash/work-server:1.2-rocm`
- `ghcr.io/attocash/work-server:1-rocm`

## Troubleshooting

### The service is using CPU workers

Check `SPRING_PROFILES_ACTIVE`. The `cpu` profile intentionally disables OpenCL workers.

### OpenCL worker startup fails

Common causes:

- The container was started without GPU device access.
- `ATTO_WORK_DEVICE` points to a missing OpenCL device.
- The selected image does not match the host GPU/runtime.

Use the image that matches the host:

- AMD: `rocm`
- NVIDIA: `cuda`
- Intel: `intel`

### Requests return `400 Bad Request`

`POST /works` rejects timestamps more than one minute in the future. Check the request timestamp and system clock.

## License

See [LICENSE](./LICENSE).
