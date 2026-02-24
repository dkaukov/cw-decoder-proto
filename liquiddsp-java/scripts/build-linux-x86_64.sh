#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LIQUID_VER="${LIQUID_VER:-1.7.0}"
SRC_DIR="$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}"
OUT_DIR="$ROOT_DIR/liquiddsp-java/src/main/resources/native/linux-x86_64"
JOBS="${JOBS:-$(getconf _NPROCESSORS_ONLN 2>/dev/null || echo 4)}"

mkdir -p "$ROOT_DIR/.tmp" "$OUT_DIR"

if [[ ! -d "$SRC_DIR" ]]; then
  curl -L --fail "https://github.com/jgaeddert/liquid-dsp/archive/refs/tags/v${LIQUID_VER}.tar.gz" \
    -o "$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}.tar.gz"
  tar -xzf "$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}.tar.gz" -C "$ROOT_DIR/.tmp"
fi

docker run --rm \
  --platform linux/amd64 \
  -v "$SRC_DIR":/src \
  -v "$OUT_DIR":/out \
  debian:bookworm bash -lc "
    set -euo pipefail
    apt-get update
    apt-get install -y --no-install-recommends cmake build-essential pkg-config
    rm -rf /src/build-linux-x86_64
    cmake -S /src -B /src/build-linux-x86_64 \
      -DCMAKE_BUILD_TYPE=Release \
      -DBUILD_SHARED_LIBS=ON \
      -DBUILD_AUTOTESTS=OFF \
      -DBUILD_EXAMPLES=OFF \
      -DBUILD_BENCHMARKS=OFF \
      -DBUILD_SANDBOX=OFF
    cmake --build /src/build-linux-x86_64 -j${JOBS}
    cp /src/build-linux-x86_64/libliquid.so* /out/
    if [ -f /out/libliquid.so.1.7.0 ]; then
      ln -sf libliquid.so.1.7.0 /out/libliquid.so
    else
      first=\$(ls /out/libliquid.so* | head -n1)
      ln -sf \$(basename \"\$first\") /out/libliquid.so
    fi
  "

echo "Built Linux x86_64 LiquidDSP into: $OUT_DIR"
