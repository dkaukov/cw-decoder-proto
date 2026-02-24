#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

"$ROOT_DIR/scripts/liquiddsp/build-linux-x86_64.sh"
"$ROOT_DIR/scripts/liquiddsp/build-linux-aarch64.sh"
"$ROOT_DIR/scripts/liquiddsp/build-windows-x86_64.sh"
"$ROOT_DIR/scripts/liquiddsp/build-windows-aarch64.sh"

echo "All requested LiquidDSP binaries built."
