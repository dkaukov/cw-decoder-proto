package io.github.dkaukov.cw_decoder_proto.liquid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sun.jna.Memory;

import org.apache.commons.math3.complex.Complex;
import org.junit.jupiter.api.Test;

class LiquidDSPFFtTest {

    private static final double TOL = 1e-3;

    @Test
    void testCreateAndDestroyFftPlan() {
        int n = 8;
        Memory in = ComplexMarshalUtils.allocateComplexBuffer(n);
        Memory out = ComplexMarshalUtils.allocateComplexBuffer(n);

        var fft = LiquidDSPFFt.INSTANCE;
        var plan = fft.fft_create_plan(n, in, out,
            LiquidDSPFFt.LIQUID_FFT_FORWARD,
            LiquidDSPFFt.LIQUID_FFT_ESTIMATE);
        assertNotNull(plan);
        fft.fft_destroy_plan(plan);
    }

    @Test
    void testFftConstantInput() {
        int n = 8;
        Complex[] input = new Complex[n];
        for (int i = 0; i < n; i++) {
            input[i] = new Complex(1.0, 0.0);
        }

        Memory in = ComplexMarshalUtils.toNative(input);
        Memory out = ComplexMarshalUtils.allocateComplexBuffer(n);

        var fft = LiquidDSPFFt.INSTANCE;
        var plan = fft.fft_create_plan(n, in, out,
            LiquidDSPFFt.LIQUID_FFT_FORWARD,
            LiquidDSPFFt.LIQUID_FFT_ESTIMATE);
        fft.fft_execute(plan);

        Complex[] result = ComplexMarshalUtils.fromNative(out, n);

        // Bin 0 should be ~n + 0j, others ~0
        assertEquals(n, result[0].abs(), TOL);
        for (int i = 1; i < n; i++) {
            assertEquals(0.0, result[i].abs(), TOL);
        }

        fft.fft_destroy_plan(plan);
    }

    @Test
    void testFftRoundTrip() {
        int n = 8;
        Complex[] original = new Complex[n];
        for (int i = 0; i < n; i++) {
            original[i] = new Complex(Math.sin(i), Math.cos(i));
        }

        // FORWARD
        Memory in = ComplexMarshalUtils.toNative(original);
        Memory spectrum = ComplexMarshalUtils.allocateComplexBuffer(n);

        var fft = LiquidDSPFFt.INSTANCE;
        var planFwd = fft.fft_create_plan(n, in, spectrum,
            LiquidDSPFFt.LIQUID_FFT_FORWARD,
            LiquidDSPFFt.LIQUID_FFT_ESTIMATE);
        fft.fft_execute(planFwd);
        fft.fft_destroy_plan(planFwd);

        // INVERSE
        Memory timeDomain = ComplexMarshalUtils.allocateComplexBuffer(n);
        var planInv = fft.fft_create_plan(n, spectrum, timeDomain,
            LiquidDSPFFt.LIQUID_FFT_BACKWARD,
            LiquidDSPFFt.LIQUID_FFT_ESTIMATE);
        fft.fft_execute(planInv);
        fft.fft_destroy_plan(planInv);

        Complex[] roundTripped = ComplexMarshalUtils.fromNative(timeDomain, n);

        // liquid-dsp IFFT is not normalized — divide by N
        for (int i = 0; i < n; i++) {
            Complex expected = original[i];
            Complex actual = roundTripped[i].divide(n);
            assertEquals(expected.getReal(), actual.getReal(), TOL, "real part at " + i);
            assertEquals(expected.getImaginary(), actual.getImaginary(), TOL, "imag part at " + i);
        }
    }

}