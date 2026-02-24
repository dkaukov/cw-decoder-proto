package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface LiquidDSPFirDesign extends Library {
  LiquidDSPFirDesign INSTANCE =
    NativeLibLoader.loadLiquidDsp(LiquidDSPFirDesign.class);

  void liquid_firdes_kaiser(int n, float fc, float As, float mu, Pointer h);
  void liquid_firdes_prototype(int ftype, int k, int m, float beta, float dt, Pointer h);
}
