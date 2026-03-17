#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LIQUID_VER="${LIQUID_VER:-1.7.0}"
SRC_DIR="$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}"
OUT_DIR="$ROOT_DIR/liquiddsp-java/src/main/resources/native/macos-aarch64"
JOBS="${JOBS:-$(sysctl -n hw.logicalcpu 2>/dev/null || echo 4)}"

mkdir -p "$ROOT_DIR/.tmp" "$OUT_DIR"

if [[ "$(uname -s)" != "Darwin" ]]; then
  echo "This script must be run on macOS." >&2
  exit 1
fi

if [[ "$(uname -m)" != "arm64" ]]; then
  echo "This script builds the macOS aarch64 binary and must be run on Apple Silicon." >&2
  exit 1
fi

if [[ ! -d "$SRC_DIR" ]]; then
  curl -L --fail "https://github.com/jgaeddert/liquid-dsp/archive/refs/tags/v${LIQUID_VER}.tar.gz" \
    -o "$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}.tar.gz"
  tar -xzf "$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}.tar.gz" -C "$ROOT_DIR/.tmp"
fi

rm -rf "$SRC_DIR/build-macos-aarch64"

cmake -S "$SRC_DIR" -B "$SRC_DIR/build-macos-aarch64" \
  -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_OSX_ARCHITECTURES=arm64 \
  -DBUILD_SHARED_LIBS=ON \
  -DBUILD_AUTOTESTS=OFF \
  -DBUILD_EXAMPLES=OFF \
  -DBUILD_BENCHMARKS=OFF \
  -DBUILD_SANDBOX=OFF \
  -DENABLE_SIMD=OFF \
  -DCMAKE_C_FLAGS="-DLIQUID_FFTOVERRIDE=1"

cmake --build "$SRC_DIR/build-macos-aarch64" -j"$JOBS"

cp "$SRC_DIR/build-macos-aarch64/libliquid.1.dylib" "$OUT_DIR/libliquid.dylib.${LIQUID_VER}"
ln -sf "libliquid.dylib.${LIQUID_VER}" "$OUT_DIR/libliquid.dylib"

echo "Built macOS aarch64 LiquidDSP into: $OUT_DIR"
