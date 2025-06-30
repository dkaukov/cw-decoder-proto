package io.github.dkaukov.cw_decoder_proto.liquid;

import com.sun.jna.Memory;

import org.apache.commons.math3.complex.Complex;

public final class ComplexMarshalUtils {

    private static final int FLOAT_SIZE = Float.BYTES;

    private ComplexMarshalUtils() {
        // Utility class, do not instantiate
    }

    /**
     * Converts a Complex[] array to a native float-complex buffer in interleaved format.
     * Format: [re0, im0, re1, im1, ..., reN, imN]
     */
    public static Memory toNative(Complex[] values) {
        Memory mem = new Memory(values.length * 2L * FLOAT_SIZE);
        for (int i = 0; i < values.length; i++) {
            long base = i * 2L * FLOAT_SIZE;
            mem.setFloat(base,        (float) values[i].getReal());
            mem.setFloat(base + FLOAT_SIZE, (float) values[i].getImaginary());
        }
        return mem;
    }

    /**
     * Reads a native float-complex buffer (interleaved) and converts it to a Complex[] array.
     */
    public static Complex[] fromNative(Memory mem, int complexLength) {
        Complex[] result = new Complex[complexLength];
        for (int i = 0; i < complexLength; i++) {
            long base = i * 2L * FLOAT_SIZE;
            float re = mem.getFloat(base);
            float im = mem.getFloat(base + FLOAT_SIZE);
            result[i] = new Complex(re, im);
        }
        return result;
    }

    /**
     * Allocates native memory for N float-complex values (i.e., 2*N floats).
     */
    public static Memory allocateComplexBuffer(int complexLength) {
        return new Memory(complexLength * 2L * FLOAT_SIZE);
    }
}