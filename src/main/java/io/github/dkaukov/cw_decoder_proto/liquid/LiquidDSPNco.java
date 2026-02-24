package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Library;
import com.sun.jna.PointerType;

public interface LiquidDSPNco extends Library {
  LiquidDSPNco INSTANCE =
    NativeLibLoader.loadLiquidDsp(LiquidDSPNco.class);

  int LIQUID_NCO = 0; // real NCO type (matches liquid enum)

  final class NcoCrcf extends PointerType {}

  NcoCrcf nco_crcf_create(int type);
  void   nco_crcf_destroy(NcoCrcf n);

  void   nco_crcf_set_frequency(NcoCrcf n, float w); // radians/sample
  void   nco_crcf_step(NcoCrcf n);

  // y = x * e^{+j*phi}; JNA maps complex as two floats; use float[2] or JNI helper
  void   nco_crcf_mix_up(NcoCrcf n, float[] x_re_im, float[] y_re_im);
}

