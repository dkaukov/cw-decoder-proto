package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface LiquidDSPSPGram extends Library {
  LiquidDSPSPGram INSTANCE =
    NativeLibLoader.loadFromResource("native/macos-aarch64/libliquid.dylib", LiquidDSPSPGram.class);

  final class Spgramcf extends PointerType {}

  Spgramcf spgramcf_create_default(int nfft);
  void     spgramcf_destroy(Spgramcf s);

  // write k complex samples
  void     spgramcf_write(Spgramcf s, Pointer x_re_im, int k);

  // fetch PSD (nfft floats)
  void     spgramcf_get_psd(Spgramcf s, Pointer out_psd);
}
