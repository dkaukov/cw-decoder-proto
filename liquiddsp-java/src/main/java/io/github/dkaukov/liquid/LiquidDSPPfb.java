package io.github.dkaukov.liquid;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface LiquidDSPPfb extends Library {

    LiquidDSPPfb INSTANCE = NativeLibLoader.loadLiquidDsp(LiquidDSPPfb.class);

    // Constants for _type argument
    int LIQUID_ANALYZER = 0;
    int LIQUID_SYNTHESIZER = 1;

    class FirpfbchCrcf extends PointerType {}

    // Create using custom filter
    FirpfbchCrcf firpfbch_crcf_create(int type, int M, int h_len, Pointer filterCoeffs);

    // Create using Kaiser window
    FirpfbchCrcf firpfbch_crcf_create_kaiser(int type, int M, int m, float As);

    // Create using Raised Nyquist filter
    FirpfbchCrcf firpfbch_crcf_create_rnyquist(int type, int M, int m, float beta, int ftype);

    // Destroy the instance
    int firpfbch_crcf_destroy(FirpfbchCrcf handle);

    // Reset internal state
    int firpfbch_crcf_reset(FirpfbchCrcf handle);

    // Print object configuration (to stdout)
    int firpfbch_crcf_print(FirpfbchCrcf handle);

    // Execute analyzer
    int firpfbch_crcf_analyzer_execute(FirpfbchCrcf handle, Pointer input, Pointer output);

    // Execute synthesizer
    int firpfbch_crcf_synthesizer_execute(FirpfbchCrcf handle, Pointer input, Pointer output);
}