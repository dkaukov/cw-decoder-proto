package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface LiquidDSPResamp extends Library {
  LiquidDSPResamp INSTANCE =
    NativeLibLoader.loadLiquidDsp(LiquidDSPResamp.class);

  final class ResampCrcf extends PointerType {}

  // r = output_rate / input_rate (e.g., Fs_target/Fs_in)
  ResampCrcf resamp_crcf_create(float r, int m, float bw, float As, int npfb);
  void       resamp_crcf_destroy(ResampCrcf r);

  // one complex input → 0..N complex output; returns ny; write into out_re_im
  int        resamp_crcf_execute(ResampCrcf r, float[] x_re_im, Pointer out_re_im);
}
