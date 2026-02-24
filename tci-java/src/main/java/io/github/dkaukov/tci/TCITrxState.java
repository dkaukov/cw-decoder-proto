package io.github.dkaukov.tci;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Component
@Slf4j
public class TCITrxState {

  @Data
  public static class ChannelState {
    private int vfo;
    private int iFace;
    private int signal;
  }

  @Data
  public static class TrxState {
    private boolean active;
    private boolean tune;
    private boolean rxEnabled = true;
    private boolean txEnabled;
    private boolean mute;
    private int dds;
    private boolean isIQEnabled;
    private String modulation;
    private Float txPowerPct;
    private Float tunePowerPct;
    private List<ChannelState> ch = new ArrayList<>();
  }

  private String protocol;
  private String protocolVersion;
  private String device;
  private int trxCount;
  private int channelCount;
  private int iqSampleRate;
  private int audioSampleRate;
  private Float txPower;
  private Float txSwr;
  private Integer ifMin;
  private Integer ifMax;
  private boolean ready;
  private Float txPowerPct;
  private Float tunePowerPct;
  private List<TrxState> trx = new ArrayList<>();

  boolean parseCommand(String cmd, String[] params) {
    switch (cmd) {
      case "ready":
        this.setReady(true);
        return true;
      case "protocol":
        this.setProtocol(params[0]);
        this.setProtocolVersion(params[1]);
        return true;
      case "device":
        this.setDevice(params[0]);
        return true;
      case "trx_count":
        this.setTrxCount(Integer.parseInt(params[0]));
        this.getTrx().clear();
        for (int i = 0; i < getTrxCount(); i++) {
          this.getTrx().add(new TrxState());
        }
        return true;
      case "channels_count":
        this.setChannelCount(Integer.parseInt(params[0]));
        this.getTrx().forEach(trx -> {
          trx.getCh().clear();
          for (int i = 0; i < getChannelCount(); i++) {
            trx.getCh().add(new ChannelState());
          }
        });
        return true;
      case "iq_samplerate":
        this.setIqSampleRate(Integer.parseInt(params[0]));
        return true;
      case "audio_samplerate":
        this.setAudioSampleRate(Integer.parseInt(params[0]));
        return true;
      case "tx_power":
        this.setTxPower(Float.parseFloat(params[0]));
        return true;
      case "tx_swr":
        this.setTxSwr(Float.parseFloat(params[0]));
        return true;
      case "if_limits":
        this.setIfMin(Integer.parseInt(params[0]));
        this.setIfMax(Integer.parseInt(params[1]));
        return true;
      case "trx":
        this.getTrx().get(Integer.parseInt(params[0])).setActive(Boolean.parseBoolean(params[1]));
        return true;
      case "tune":
        this.getTrx().get(Integer.parseInt(params[0])).setTune(Boolean.parseBoolean(params[1]));
        return true;
      case "dds":
        this.getTrx().get(Integer.parseInt(params[0])).setDds(Integer.parseInt(params[1]));
        return true;
      case "if":
        this.getTrx()
            .get(Integer.parseInt(params[0])).getCh()
            .get(Integer.parseInt(params[1]))
            .setIFace(Integer.parseInt(params[2]));
        return true;
      case "vfo":
        this.getTrx()
            .get(Integer.parseInt(params[0])).getCh()
            .get(Integer.parseInt(params[1]))
            .setVfo(Integer.parseInt(params[2]));
        return true;
      case "modulation":
        this.getTrx().get(Integer.parseInt(params[0])).setModulation(params[1]);
        return true;
      case "rx_smeter":
        this.getTrx()
            .get(Integer.parseInt(params[0])).getCh()
            .get(Integer.parseInt(params[1]))
            .setSignal(Integer.parseInt(params[2]));
        return true;
      case "iq_stop":
        this.getTrx().get(Integer.parseInt(params[0])).setIQEnabled(false);
        return true;
      case "iq_start":
        this.getTrx().get(Integer.parseInt(params[0])).setIQEnabled(true);
        return true;
      case "rx_enable":
        this.getTrx().get(Integer.parseInt(params[0])).setRxEnabled(Boolean.parseBoolean(params[1]));
        return true;
      case "rx_mute":
        this.getTrx().get(Integer.parseInt(params[0])).setMute(Boolean.parseBoolean(params[1]));
        return true;
      case "tx_enable":
        this.getTrx().get(Integer.parseInt(params[0])).setTxEnabled(Boolean.parseBoolean(params[1]));
        return true;
      case "drive":
        if (params.length == 1) {
          this.setTxPowerPct(Float.parseFloat(params[0]));
        } else {
          this.getTrx().get(Integer.parseInt(params[0])).setTxPowerPct(Float.parseFloat(params[1]));
        }
        return true;
      case "tune_drive":
        if (params.length == 1) {
          this.setTunePowerPct(Float.parseFloat(params[0]));
        } else {
          this.getTrx().get(Integer.parseInt(params[0])).setTunePowerPct(Float.parseFloat(params[1]));
        }
        return true;
      default:
        return false;
    }
  }

}
