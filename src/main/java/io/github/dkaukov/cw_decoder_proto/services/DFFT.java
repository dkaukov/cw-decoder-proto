package io.github.dkaukov.cw_decoder_proto.services;

import com.github.psambit9791.jdsp.transform.FastFourier;

import org.apache.commons.math3.complex.Complex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.github.dkaukov.cw_decoder_proto.tci.TCIIQData;
import io.github.dkaukov.cw_decoder_proto.tci.TCITrxState;

@Service
public class DFFT {

  @Autowired
  private TCITrxState trxState;

  @EventListener
  public void OnIQ(TCIIQData data) {
    FastFourier ft = new FastFourier(data.getSignal().stream()
        .mapToDouble(Complex::getReal)
        .toArray());
    ft.transform();
    double[] power = ft.getMagnitude(true);
    double[] freq = ft.getFFTFreq(data.getSampleRate(), true);

    ft = new FastFourier(data.getSignal().stream()
        .mapToDouble(Complex::getImaginary)
        .toArray());
    ft.transform();
    double[] powerIm = ft.getMagnitude(true);
    double[] freqIm = ft.getFFTFreq(data.getSampleRate(), true);
  }

}
