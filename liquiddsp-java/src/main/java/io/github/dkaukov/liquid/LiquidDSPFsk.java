package io.github.dkaukov.liquid;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface LiquidDSPFsk extends Library {
  LiquidDSPFsk INSTANCE =
    NativeLibLoader.loadLiquidDsp(LiquidDSPFsk.class);

  final class Fskdem extends PointerType {}

  // m = bits/symbol (1 for 2-FSK), k = samples/symbol, bw = Δf/Rsym_eff (< 0.5)
  Fskdem fskdem_create(int m, int k, float bw);
  void   fskdem_destroy(Fskdem d);

  // input: one symbol’s worth of complex samples (length k). Returns symbol (0..M-1)
  int    fskdem_demodulate(Fskdem d, Pointer symbuf_re_im);
}
