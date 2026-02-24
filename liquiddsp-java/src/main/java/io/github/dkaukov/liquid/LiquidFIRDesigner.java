package io.github.dkaukov.liquid;

import com.sun.jna.Memory;

public final class LiquidFIRDesigner {
  // liquid_firfilt_type enum value for root-raised-cosine prototype
  public static final int LIQUID_FIRFILT_RRCOS = 7;

  private LiquidFIRDesigner() {}

  public static float[] designKaiserLowpass(int n, float fc, float as, float mu) {
    if (n <= 0) {
      throw new IllegalArgumentException("n must be > 0");
    }
    Memory taps = new Memory((long) n * Float.BYTES);
    LiquidDSPFirDesign.INSTANCE.liquid_firdes_kaiser(n, fc, as, mu, taps);
    return readFloatArray(taps, n);
  }

  public static float[] designPrototype(int ftype, int k, int m, float beta, float dt) {
    if (k <= 0 || m <= 0) {
      throw new IllegalArgumentException("k and m must be > 0");
    }
    int hLen = 2 * k * m + 1;
    Memory taps = new Memory((long) hLen * Float.BYTES);
    LiquidDSPFirDesign.INSTANCE.liquid_firdes_prototype(ftype, k, m, beta, dt, taps);
    return readFloatArray(taps, hLen);
  }

  public static float[] designRrc(int k, int m, float beta, float dt) {
    return designPrototype(LIQUID_FIRFILT_RRCOS, k, m, beta, dt);
  }

  private static float[] readFloatArray(Memory mem, int n) {
    float[] out = new float[n];
    for (int i = 0; i < n; i++) {
      out[i] = mem.getFloat((long) i * Float.BYTES);
    }
    return out;
  }
}
