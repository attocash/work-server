# Work server

![Build](https://github.com/attocash/work-server/workflows/Build/badge.svg)

This project is a dedicated work server for the [Atto cryptocurrency](https://github.com/attocash). See the [documentation](https://github.com/attocash/) for details on work generation.

**work-server** supports the `work_generate`, `work_cancel`, and `work_validate` commands.

To see available command line options, run `work-server --help`.

If using more than one work peer, give the flag `--shuffle`. This makes it so that the next request is picked randomly instead of sequentially, which leads to more efficient work generation with multiple peers, especially when they are not in the same network.

## Installation

### OpenCL 

Ubuntu:

```
sudo apt install ocl-icd-opencl-dev
```

Fedora:

```
sudo dnf install ocl-icd-devel
```

Windows:
- AMD GPU: [OCL-SDK](https://github.com/GPUOpen-LibrariesAndSDKs/OCL-SDK/releases/)
- Nvidia GPU: [CUDA Toolkit](https://developer.nvidia.com/cuda-toolkit)

### Rust

Linux:

```
curl https://sh.rustup.rs -sSf | sh
```

Windows: follow instructions in https://www.rust-lang.org/tools/install

### GCC

Ubuntu:

```
sudo apt install gcc
```

Fedora:

```
sudo dnf install gcc
```

### Build

```bash
git clone https://github.com/attocash/work-server.git
cd work-server
cargo build --release
```

Depending on your system configuration and if the OpenCL library cannot be found in the `PATH`, it may be necessary to link against explicitly:

```bash
cargo rustc --release -- -l OpenCL -L "/path/to/opencl.lib"
```

## Using

`work-server --help`

_Note_ threshold values may be outdated in these examples.

- `work_generate` example:

    ```json
    {
        "action": "work_generate",
        "hash": "718CC2121C3E641059BC1C2CFC45666C99E8AE922F7A807B7D07B62C995D79E2",
        "threshold": "34359738367"
    }
    ```
    Response:

    ```json
    {
        "work": "2bf29ef00786a6bc",
        "threshold": "34359738367"     
    }
    ```


- `work_validate` example:

    ```json
    {
        "action": "work_validate",
        "hash": "718CC2121C3E641059BC1C2CFC45666C99E8AE922F7A807B7D07B62C995D79E2",
        "work": "2bf29ef00786a6bc",
        "threshold": "34359738367"
    }
    ```
    Response:

    ```json
    {
        "valid": "1",
        "threshold": "34359738367"
    }
    ```

- `work_cancel` example:
    ```json
    {
        "action": "work_cancel",
        "hash": "718CC2121C3E641059BC1C2CFC45666C99E8AE922F7A807B7D07B62C995D79E2"
    }
    ```
    Response:

    ```json
    {
    }
    ```

## Benchmarking

Example request:

```json
{
    "action": "benchmark",
    "threshold": "34359738367",
    "count": "10"
}
```

_Note_ use a sufficiently high count as work generation is a random process.

Example response:

```json
{
    "average": "481",
    "count": "10",
    "threshold": "34359738367",
    "duration": "4813",
    "hint": "Times in milliseconds",
    "multiplier": "1"
}
```

## Status

Example request:

```json
{
    "action": "status"
}
```

Example response:

```json
{
    "generating": "1",
    "queue_size": "3"
}
```

## Troubleshooting

- Linux OpenCL AMD GPU series error: `thread 'main' panicked at 'Failed to create GPU from string "00:00"` - see [solution here](https://github.com/nanocurrency/nano-work-server/issues/28)
