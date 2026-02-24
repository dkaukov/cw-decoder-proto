#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LIQUID_VER="${LIQUID_VER:-1.7.0}"
SRC_DIR="$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}"
OUT_DIR="$ROOT_DIR/liquiddsp-java/src/main/resources/native/windows-x86_64"
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
    apt-get install -y --no-install-recommends cmake mingw-w64 g++-mingw-w64-x86-64 pkg-config make
    cp -a /src /work
    rm -rf /work/build-windows-x86_64
    sed -i 's|^include(cmake/FindSIMD.cmake)|# disabled for MinGW cross-build: include(cmake/FindSIMD.cmake)|' /work/CMakeLists.txt
    sed -i 's|target_link_libraries(\${LIBNAME} c m)|target_link_libraries(\${LIBNAME} m)|' /work/CMakeLists.txt
    cmake -S /work -B /work/build-windows-x86_64 \
      -DCMAKE_SYSTEM_NAME=Windows \
      -DCMAKE_C_COMPILER=x86_64-w64-mingw32-gcc \
      -DCMAKE_CXX_COMPILER=x86_64-w64-mingw32-g++ \
      -DCMAKE_RC_COMPILER=x86_64-w64-mingw32-windres \
      -DCMAKE_BUILD_TYPE=Release \
      -DENABLE_SIMD=OFF \
      -DBUILD_SHARED_LIBS=ON \
      -DBUILD_AUTOTESTS=OFF \
      -DBUILD_EXAMPLES=OFF \
      -DBUILD_BENCHMARKS=OFF \
      -DBUILD_SANDBOX=OFF
    cmake --build /work/build-windows-x86_64 -j${JOBS}
    dll=\$(find /work/build-windows-x86_64 -name 'libliquid*.dll' -o -name 'liquid*.dll' | head -n1)
    if [ -z \"\$dll\" ]; then
      echo 'Could not find built DLL artifact' >&2
      exit 1
    fi
    cp \"\$dll\" /out/liquid.dll
  "

echo "Built Windows x86_64 LiquidDSP into: $OUT_DIR"
