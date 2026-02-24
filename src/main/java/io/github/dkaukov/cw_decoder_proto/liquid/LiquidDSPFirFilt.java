package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface LiquidDSPFirFilt extends Library {
  LiquidDSPFirFilt INSTANCE =
    NativeLibLoader.loadFromResource("native/macos-aarch64/libliquid.dylib", LiquidDSPFirFilt.class);

  final class FirfiltCrcf extends PointerType {}

  FirfiltCrcf firfilt_crcf_create(Pointer h, int hLen);
  FirfiltCrcf firfilt_crcf_create_kaiser(int n, float fc, float As, float mu);

  int  firfilt_crcf_reset(FirfiltCrcf f);
  int  firfilt_crcf_destroy(FirfiltCrcf f);
  int  firfilt_crcf_get_length(FirfiltCrcf f);

  void firfilt_crcf_execute_one(FirfiltCrcf f, float[] xReIm, float[] yReIm);
  void firfilt_crcf_execute_block(FirfiltCrcf f, Pointer xReIm, int n, Pointer yReIm);
}
