# Bundled LiquidDSP Binaries

`NativeLibLoader` resolves LiquidDSP by OS/architecture and expects one of these paths:

- `native/macos-aarch64/libliquid.dylib`
- `native/macos-x86_64/libliquid.dylib`
- `native/linux-aarch64/libliquid.so`
- `native/linux-x86_64/libliquid.so`
- `native/windows-aarch64/liquid.dll`
- `native/windows-x86_64/liquid.dll`

Current repository contents include only:

- `native/macos-aarch64/libliquid.dylib` (symlink to versioned file)

To build and add other platforms with Docker:

- `scripts/liquiddsp/build-linux-x86_64.sh`
- `scripts/liquiddsp/build-linux-aarch64.sh`
- `scripts/liquiddsp/build-windows-x86_64.sh`
- `scripts/liquiddsp/build-windows-aarch64.sh`
- `scripts/liquiddsp/build-all.sh`

The scripts default to LiquidDSP `v1.7.0`. Override with:

- `LIQUID_VER=1.7.0 scripts/liquiddsp/build-linux-x86_64.sh`
