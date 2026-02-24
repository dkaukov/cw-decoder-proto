package io.github.dkaukov.liquid;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface LiquidDSPSymsync extends Library {
  LiquidDSPSymsync INSTANCE =
    NativeLibLoader.loadLiquidDsp(LiquidDSPSymsync.class);

  // filter type for rnyquist
  int LIQUID_FIRFILT_RRC = 5; // (value per liquid-dsp enum)

  final class SymsyncCrcf extends PointerType {}

  SymsyncCrcf symsync_crcf_create_rnyquist(int ftype, int k, int m, float beta, int npfb);
  void        symsync_crcf_destroy(SymsyncCrcf s);
  void        symsync_crcf_reset(SymsyncCrcf s);

  // execute on one sample (complex). Output 0..N samples into out buffer.
  int         symsync_crcf_execute(SymsyncCrcf s, float[] x_re_im, int nx, Pointer out_re_im, Pointer ny_written);
}
