package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Library;
import com.sun.jna.PointerType;

public interface LiquidDSPIirFilt extends Library {
  LiquidDSPIirFilt INSTANCE =
    NativeLibLoader.loadFromResource("native/macos-aarch64/libliquid.dylib", LiquidDSPIirFilt.class);

  final class IirfiltCrcf extends PointerType {}

  IirfiltCrcf iirfilt_crcf_create_lowpass(int order, float fc); // fc normalized (0..0.5)
  void        iirfilt_crcf_destroy(IirfiltCrcf f);
  void        iirfilt_crcf_reset(IirfiltCrcf f);

  // one complex in → one complex out (re,im arrays length 2)
  void        iirfilt_crcf_execute(IirfiltCrcf f, float[] x_re_im, float[] y_re_im);
}

