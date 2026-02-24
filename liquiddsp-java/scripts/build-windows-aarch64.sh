#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LIQUID_VER="${LIQUID_VER:-1.7.0}"
LLVM_MINGW_TAG="${LLVM_MINGW_TAG:-20251216}"
LLVM_MINGW_NAME="llvm-mingw-${LLVM_MINGW_TAG}-ucrt-ubuntu-22.04-x86_64"
LLVM_MINGW_ARCHIVE="${LLVM_MINGW_NAME}.tar.xz"

SRC_DIR="$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}"
TOOLCHAIN_DIR="$ROOT_DIR/.tmp/${LLVM_MINGW_NAME}"
OUT_DIR="$ROOT_DIR/liquiddsp-java/src/main/resources/native/windows-aarch64"
JOBS="${JOBS:-$(getconf _NPROCESSORS_ONLN 2>/dev/null || echo 4)}"

mkdir -p "$ROOT_DIR/.tmp" "$OUT_DIR"

if [[ ! -d "$SRC_DIR" ]]; then
  curl -L --fail "https://github.com/jgaeddert/liquid-dsp/archive/refs/tags/v${LIQUID_VER}.tar.gz" \
    -o "$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}.tar.gz"
  tar -xzf "$ROOT_DIR/.tmp/liquid-dsp-${LIQUID_VER}.tar.gz" -C "$ROOT_DIR/.tmp"
fi

if [[ ! -d "$TOOLCHAIN_DIR" ]]; then
  curl -L --fail "https://github.com/mstorsjo/llvm-mingw/releases/download/${LLVM_MINGW_TAG}/${LLVM_MINGW_ARCHIVE}" \
    -o "$ROOT_DIR/.tmp/${LLVM_MINGW_ARCHIVE}"
  tar -xJf "$ROOT_DIR/.tmp/${LLVM_MINGW_ARCHIVE}" -C "$ROOT_DIR/.tmp"
fi

docker run --rm \
  --platform linux/amd64 \
  -v "$SRC_DIR":/src \
  -v "$TOOLCHAIN_DIR":/toolchain \
  -v "$OUT_DIR":/out \
  debian:bookworm bash -lc "
    set -euo pipefail
    apt-get update
    apt-get install -y --no-install-recommends cmake make
    cp -a /src /work
    rm -rf /work/build-windows-aarch64
    sed -i 's|^include(cmake/FindSIMD.cmake)|# disabled for Windows ARM64 cross-build: include(cmake/FindSIMD.cmake)|' /work/CMakeLists.txt
    sed -i 's|target_link_libraries(\${LIBNAME} c m)|target_link_libraries(\${LIBNAME} m)|' /work/CMakeLists.txt
    cmake -S /work -B /work/build-windows-aarch64 \
      -DCMAKE_SYSTEM_NAME=Windows \
      -DCMAKE_SYSTEM_PROCESSOR=ARM64 \
      -DCMAKE_C_COMPILER=/toolchain/bin/aarch64-w64-mingw32-clang \
      -DCMAKE_CXX_COMPILER=/toolchain/bin/aarch64-w64-mingw32-clang++ \
      -DCMAKE_RC_COMPILER=/toolchain/bin/llvm-rc \
      -DCMAKE_AR=/toolchain/bin/llvm-ar \
      -DCMAKE_RANLIB=/toolchain/bin/llvm-ranlib \
      -DCMAKE_BUILD_TYPE=Release \
      -DENABLE_SIMD=OFF \
      -DBUILD_SHARED_LIBS=ON \
      -DBUILD_AUTOTESTS=OFF \
      -DBUILD_EXAMPLES=OFF \
      -DBUILD_BENCHMARKS=OFF \
      -DBUILD_SANDBOX=OFF
    cmake --build /work/build-windows-aarch64 -j${JOBS}
    dll=\$(find /work/build-windows-aarch64 -name 'libliquid*.dll' -o -name 'liquid*.dll' | head -n1)
    if [ -z \"\$dll\" ]; then
      echo 'Could not find built DLL artifact' >&2
      exit 1
    fi
    cp \"\$dll\" /out/liquid.dll
  "

echo "Built Windows aarch64 LiquidDSP into: $OUT_DIR"
