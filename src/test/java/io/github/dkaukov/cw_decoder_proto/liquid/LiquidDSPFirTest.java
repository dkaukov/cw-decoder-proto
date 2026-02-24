package io.github.dkaukov.cw_decoder_proto.liquid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.jna.Memory;
import org.apache.commons.math3.complex.Complex;
import org.junit.jupiter.api.Test;

class LiquidDSPFirTest {

  @Test
  void designKaiserLowpass_ProducesFiniteSymmetricTaps() {
    int n = 65;
    float[] taps = LiquidFIRDesigner.designKaiserLowpass(n, 0.08f, 60.0f, 0.0f);
    assertEquals(n, taps.length);

    for (int i = 0; i < taps.length; i++) {
      assertTrue(Float.isFinite(taps[i]), "tap must be finite");
    }

    for (int i = 0; i < n / 2; i++) {
      assertEquals(taps[i], taps[n - 1 - i], 1e-4, "taps should be symmetric");
    }
  }

  @Test
  void firFilter_ConstantSignalConvergesToDcGain() {
    float[] taps = new float[] {0.25f, 0.25f, 0.25f, 0.25f};
    Memory h = new Memory((long) taps.length * Float.BYTES);
    for (int i = 0; i < taps.length; i++) {
      h.setFloat((long) i * Float.BYTES, taps[i]);
    }

    var fir = LiquidDSPFirFilt.INSTANCE.firfilt_crcf_create(h, taps.length);
    assertEquals(taps.length, LiquidDSPFirFilt.INSTANCE.firfilt_crcf_get_length(fir));

    int n = 32;
    Complex[] input = new Complex[n];
    for (int i = 0; i < n; i++) {
      input[i] = new Complex(1.0, 0.0);
    }

    Memory in = ComplexMarshalUtils.toNative(input);
    Memory out = ComplexMarshalUtils.allocateComplexBuffer(n);
    LiquidDSPFirFilt.INSTANCE.firfilt_crcf_execute_block(fir, in, n, out);
    Complex[] y = ComplexMarshalUtils.fromNative(out, n);

    assertEquals(1.0, y[n - 1].getReal(), 1e-3);
    assertEquals(0.0, y[n - 1].getImaginary(), 1e-3);
    LiquidDSPFirFilt.INSTANCE.firfilt_crcf_destroy(fir);
  }
}
