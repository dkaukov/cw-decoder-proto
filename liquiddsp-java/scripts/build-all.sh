#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

"$ROOT_DIR/liquiddsp-java/scripts/build-linux-x86_64.sh"
"$ROOT_DIR/liquiddsp-java/scripts/build-linux-aarch64.sh"
"$ROOT_DIR/liquiddsp-java/scripts/build-windows-x86_64.sh"
"$ROOT_DIR/liquiddsp-java/scripts/build-windows-aarch64.sh"
if [[ "$(uname -s)" == "Darwin" && "$(uname -m)" == "arm64" ]]; then
  "$ROOT_DIR/liquiddsp-java/scripts/build-macos-aarch64.sh"
fi

echo "All requested LiquidDSP binaries built."
