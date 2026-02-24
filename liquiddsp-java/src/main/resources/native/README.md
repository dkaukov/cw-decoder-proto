# Bundled LiquidDSP Binaries

`NativeLibLoader` resolves LiquidDSP by OS/architecture and expects one of these paths:

- `native/macos-aarch64/libliquid.dylib`
- `native/macos-x86_64/libliquid.dylib`
- `native/linux-aarch64/libliquid.so`
- `native/linux-x86_64/libliquid.so`
- `native/windows-aarch64/liquid.dll`
- `native/windows-x86_64/liquid.dll`

Current repository contents include:

- `native/macos-aarch64/libliquid.dylib` (symlink to versioned file)
- `native/linux-x86_64/libliquid.so`
- `native/linux-aarch64/libliquid.so`
- `native/windows-x86_64/liquid.dll`
- `native/windows-aarch64/liquid.dll`

To build and add other platforms with Docker:

- `liquiddsp-java/scripts/build-linux-x86_64.sh`
- `liquiddsp-java/scripts/build-linux-aarch64.sh`
- `liquiddsp-java/scripts/build-windows-x86_64.sh`
- `liquiddsp-java/scripts/build-windows-aarch64.sh`
- `liquiddsp-java/scripts/build-all.sh`

The scripts default to LiquidDSP `v1.7.0`. Override with:

- `LIQUID_VER=1.7.0 liquiddsp-java/scripts/build-linux-x86_64.sh`
