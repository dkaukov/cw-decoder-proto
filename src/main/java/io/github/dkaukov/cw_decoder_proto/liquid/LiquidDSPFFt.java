package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface LiquidDSPFFt extends Library {

    LiquidDSPFFt INSTANCE = NativeLibLoader.loadFromResource("native/macos-aarch64/libliquid.dylib", LiquidDSPFFt.class);

    // Direction constants
    int LIQUID_FFT_FORWARD = 0;
    int LIQUID_FFT_BACKWARD = 1;

    // Flags (typically 0)
    int LIQUID_FFT_ESTIMATE = 0;

    // Opaque handle
    class FftPlan extends PointerType {

    }

    // Create FFT plan
    FftPlan fft_create_plan(int n, Pointer x, Pointer y, int direction, int flags);

    // Execute plan
    void fft_execute(FftPlan plan);

    // Destroy plan
    void fft_destroy_plan(FftPlan plan);

}
