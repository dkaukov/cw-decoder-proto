package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Library;
import com.sun.jna.PointerType;

public interface LiquidDSPAgc extends Library {
  LiquidDSPAgc INSTANCE =
    NativeLibLoader.loadFromResource("native/macos-aarch64/libliquid.dylib", LiquidDSPAgc.class);

  // squelch statuses (mirror liquid-dsp)
  int LIQUID_AGC_SQUELCH_ENABLED = 1;
  int LIQUID_AGC_SQUELCH_RISE    = 2;
  int LIQUID_AGC_SQUELCH_FALL    = 3;
  int LIQUID_AGC_SQUELCH_TIMEOUT = 4;

  final class AgcCrcf extends PointerType {}

  AgcCrcf agc_crcf_create();
  void    agc_crcf_destroy(AgcCrcf a);

  void    agc_crcf_set_bandwidth(AgcCrcf a, float bw);
  void    agc_crcf_set_signal_level(AgcCrcf a, float target);

  void    agc_crcf_squelch_enable(AgcCrcf a);
  void    agc_crcf_squelch_disable(AgcCrcf a);
  void    agc_crcf_squelch_set_threshold(AgcCrcf a, float threshold_dB);
  void    agc_crcf_squelch_set_timeout(AgcCrcf a, int timeout);
  int     agc_crcf_squelch_get_status(AgcCrcf a);

  void    agc_crcf_execute(AgcCrcf a, float[] x_re_im, float[] y_re_im);
}
