package io.github.dkaukov.cw_decoder_proto.liquid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import com.sun.jna.Memory;

import org.apache.commons.math3.complex.Complex;
import org.junit.jupiter.api.Test;

public class LiquidDSPPfbTest {

    private static final double TOLERANCE = 1e-2;

    /**
     * Test PFB analyzer with a pure tone input.
     * <p>
     * A sinusoidal signal is synthesized that falls exactly on bin 2 of an 8-channel
     * analyzer. After passing this signal through the filter bank repeatedly to allow
     * internal state to settle, the output bins are examined.
     * <p>
     * Expected result:
     * <ul>
     *     <li>Bin 2 shows significant energy (magnitude &gt; 0.1)</li>
     *     <li>All other bins are near zero (magnitude &lt; 1e-2)</li>
     * </ul>
     * This test ensures correct demultiplexing by the analyzer and verifies that
     * the Kaiser window provides sufficient channel isolation.
     */
    @Test
    void testPfbExecuteWithSingleTone_UsingKaiser() {
        int M = 8;                  // Number of channels
        int m = 8;                  // Semi-length of prototype filter
        int repetitions = 100;      // Number of frames to allow filter to settle
        float As = 60.0f;           // Stopband attenuation

        int toneBin = 2;            // Tone centered at bin 2
        Complex[] toneFrame = new Complex[M];
        for (int i = 0; i < M; i++) {
            double phase = 2 * Math.PI * toneBin * i / M;
            toneFrame[i] = new Complex(Math.cos(phase), Math.sin(phase)); // e^(jωt)
        }
        // Create analyzer using Kaiser window
        var pfb = LiquidDSPPfb.INSTANCE.firpfbch_crcf_create_kaiser(
            LiquidDSPPfb.LIQUID_ANALYZER,
            M, m, As
        );
        assertNotNull(pfb, "Failed to create PFB analyzer");
        Complex[] lastFrame = null;
        // Feed same frame multiple times
        for (int rep = 0; rep < repetitions; rep++) {
            var in = ComplexMarshalUtils.toNative(toneFrame);
            var out = ComplexMarshalUtils.allocateComplexBuffer(M);
            int res = LiquidDSPPfb.INSTANCE.firpfbch_crcf_analyzer_execute(pfb, in, out);
            assertEquals(0, res, "Analyzer execution failed at repetition " + rep);
            lastFrame = ComplexMarshalUtils.fromNative(out, M);
        }
        assertNotNull(lastFrame, "Output was never captured");
        // Expect energy only in toneBin
        for (int i = 0; i < M; i++) {
            double mag = lastFrame[i].abs();
            if (i == toneBin) {
                assertTrue(mag > 0.1, "Expected bin " + i + " to have energy, got " + mag);
            } else {
                assertTrue(mag < TOLERANCE, "Expected bin " + i + " to be near zero, got " + mag);
            }
        }
        LiquidDSPPfb.INSTANCE.firpfbch_crcf_destroy(pfb);
    }

    @Test
    void testCreateAndDestroy() {
        int M = 4;
        int h_len = 32;
        float[] h = new float[h_len];
        Arrays.fill(h, 0.1f);
        Memory hMem = new Memory(h_len * Float.BYTES);
        for (int i = 0; i < h_len; i++) {
            hMem.setFloat(i * Float.BYTES, h[i]);
        }
        var handle = LiquidDSPPfb.INSTANCE.firpfbch_crcf_create(LiquidDSPPfb.LIQUID_ANALYZER, M, h_len, hMem);
        assertNotNull(handle);
        LiquidDSPPfb.INSTANCE.firpfbch_crcf_destroy(handle);
    }

    /**
     * Test to determine if Liquid-DSP PFB analyzer output is FFT-shifted.
     * A tone at bin -2 should show up at:
     * - bin (M - 2) = 6 if output is FFT-shifted (DC centered at M/2)
     * - bin 6 (also) in natural order due to modulo wraparound
     *
     * So we inject tones at a few different bins to verify layout.
     */
    @Test
    void testPfbAnalyzerBinOrderDetection() {
        int M = 8;          // Number of PFB channels
        int p = 4;          // Filter semi-length
        int hLen = M * p;
        int toneBin = -2;   // Logical bin position (wrapped)
        // Create prototype filter: Hann window
        float[] h = new float[hLen];
        for (int i = 0; i < hLen; i++) {
            h[i] = (float) (0.42 - 0.5 * Math.cos(2 * Math.PI * i / (hLen - 1)));
        }
        Memory hMem = new Memory(hLen * Float.BYTES);
        for (int i = 0; i < hLen; i++) {
            hMem.setFloat((long) i * Float.BYTES, h[i]);
        }
        // Generate complex tone at bin -2 (wraps around to bin 6)
        Complex[] tone = new Complex[M];
        for (int i = 0; i < M; i++) {
            double phase = 2 * Math.PI * toneBin * i / M;
            tone[i] = new Complex(Math.cos(phase), Math.sin(phase));
        }
        var pfb = LiquidDSPPfb.INSTANCE.firpfbch_crcf_create(
            LiquidDSPPfb.LIQUID_ANALYZER, M, p, hMem);
        assertNotNull(pfb);
        Complex[] lastFrame = null;
        for (int i = 0; i < 20; i++) {
            Memory in = ComplexMarshalUtils.toNative(tone);
            Memory out = ComplexMarshalUtils.allocateComplexBuffer(M);
            LiquidDSPPfb.INSTANCE.firpfbch_crcf_analyzer_execute(pfb, in, out);
            lastFrame = ComplexMarshalUtils.fromNative(out, M);
        }
        final Complex[] lf = Arrays.copyOf(lastFrame, lastFrame.length);
        // Locate peak bin
        assertNotNull(lastFrame);
        int peakBin = IntStream.range(0, M)
            .boxed()
            .max(Comparator.comparingDouble(i -> lf[i].abs()))
            .orElseThrow();
        // Compute expected peak bin index assuming FFT-shifted layout
        int expectedFftShifted = (toneBin + M) % M;
        System.out.printf("Tone at bin %d resulted in peak at bin %d%n", toneBin, peakBin);
        assertEquals(expectedFftShifted, peakBin, "Expected peak at bin " + expectedFftShifted + " for FFT-shifted output");
        LiquidDSPPfb.INSTANCE.firpfbch_crcf_destroy(pfb);
    }

    @Test
    void testPfbAnalyzerOutputOrderDetection() {
        int M = 8;              // Number of channels
        int p = 8;              // Filter semi-length
        int h_len = M * p;      // Total number of taps
        int repetitions = 30;   // Input repetitions for filter to settle
        // Generate raised cosine prototype taps (to avoid leakage)
        float[] h = new float[h_len];
        for (int i = 0; i < h_len; i++) {
            double t = (double) i / (h_len - 1);
            h[i] = (float) (0.5 - 0.5 * Math.cos(2 * Math.PI * t)); // Hann
        }
        Memory hMem = new Memory((long) h_len * Float.BYTES);
        for (int i = 0; i < h_len; i++) {
            hMem.setFloat((long) i * Float.BYTES, h[i]);
        }
        var pfb = LiquidDSPPfb.INSTANCE.firpfbch_crcf_create(
            LiquidDSPPfb.LIQUID_ANALYZER, M, p, hMem);
        assertNotNull(pfb);
        Map<Integer, Integer> toneToPeakBin = new HashMap<>();
        for (int logicalBin = -M / 2; logicalBin < M / 2; logicalBin++) {
            Complex[] singleFrame = new Complex[M];
            for (int i = 0; i < M; i++) {
                double phase = 2 * Math.PI * logicalBin * i / M;
                singleFrame[i] = new Complex(Math.cos(phase), Math.sin(phase));
            }
            Complex[] output = null;
            for (int rep = 0; rep < repetitions; rep++) {
                Memory in = ComplexMarshalUtils.toNative(singleFrame);
                Memory out = ComplexMarshalUtils.allocateComplexBuffer(M);
                int result = LiquidDSPPfb.INSTANCE.firpfbch_crcf_analyzer_execute(pfb, in, out);
                assertEquals(0, result);
                output = ComplexMarshalUtils.fromNative(out, M);
            }
            final Complex[] lf = Arrays.copyOf(output, output.length);
            // Detect max-energy bin
            int peakBin = IntStream.range(0, M)
                .boxed()
                .max(Comparator.comparingDouble(i -> lf[i].abs()))
                .orElseThrow();
            toneToPeakBin.put(logicalBin, peakBin);
        }
        LiquidDSPPfb.INSTANCE.firpfbch_crcf_destroy(pfb);
        // Detect mapping style
        boolean looksLikeFftShifted = toneToPeakBin.entrySet().stream().allMatch(e -> e.getValue() == (e.getKey() + M) % M); // Unshifted
        boolean looksLikeFftCentered = toneToPeakBin.entrySet().stream().allMatch(e -> e.getValue() == (e.getKey() + M / 2 + M) % M); // Shifted
        System.out.println("Tone to peak bin map: " + toneToPeakBin);
        if (looksLikeFftCentered) {
            System.out.println("✅ Output bins appear FFT-shifted (DC at M/2)");
        } else if (looksLikeFftShifted) {
            System.out.println("✅ Output bins appear unshifted (DC at bin 0)");
        } else {
            System.err.println("❌ Unable to determine output bin order: inconsistent mapping");
        }
    }


}