package io.github.dkaukov.tci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest(classes = TCITrxStateTest.TestConfig.class)
class TCITrxStateTest {

  @SpringBootConfiguration
  @ComponentScan(basePackages = "io.github.dkaukov.tci")
  static class TestConfig {
  }

  @Autowired
  private TCITrxState tciTrxState;

  @Autowired
  private TCIWebSocketHandler tciWebSocketHandler;

  @Test
  void parseDevice() {
    tciWebSocketHandler.parseMessage("device:MySDR;");
    assertEquals("MySDR", tciTrxState.getDevice());
  }

  @Test
  void parseTrxCount() {
    tciWebSocketHandler.parseMessage("trx_count:2;");
    assertEquals(2, tciTrxState.getTrxCount());
    assertEquals(2, tciTrxState.getTrx().size());
  }

  @Test
  void parseChannelsCount() {
    tciWebSocketHandler.parseMessage("trx_count:1;");
    tciWebSocketHandler.parseMessage("channels_count:3;");
    assertEquals(3, tciTrxState.getChannelCount());
    assertEquals(3, tciTrxState.getTrx().get(0).getCh().size());
  }

  @Test
  void parseIqSampleRate() {
    tciWebSocketHandler.parseMessage("iq_samplerate:192000;");
    assertEquals(192000, tciTrxState.getIqSampleRate());
  }

  @Test
  void parseAudioSampleRate() {
    tciWebSocketHandler.parseMessage("audio_samplerate:48000;");
    assertEquals(48000, tciTrxState.getAudioSampleRate());
  }

  @Test
  void parseDDS() {
    tciWebSocketHandler.parseMessage("trx_count:1;");
    tciWebSocketHandler.parseMessage("dds:0,145000000;");
    assertEquals(145000000, tciTrxState.getTrx().get(0).getDds());
  }

  @Test
  void parseIfaceAndVfo() {
    tciWebSocketHandler.parseMessage("trx_count:1;");
    tciWebSocketHandler.parseMessage("channels_count:1;");
    tciWebSocketHandler.parseMessage("if:0,0,123;");
    tciWebSocketHandler.parseMessage("vfo:0,0,1;");
    assertEquals(123, tciTrxState.getTrx().get(0).getCh().get(0).getIFace());
    assertEquals(1, tciTrxState.getTrx().get(0).getCh().get(0).getVfo());
  }

  @Test
  void parseModulation() {
    tciWebSocketHandler.parseMessage("trx_count:1;");
    tciWebSocketHandler.parseMessage("modulation:0,USB;");
    assertEquals("USB", tciTrxState.getTrx().get(0).getModulation());
  }

  @Test
  void parseIqStartStop() {
    tciWebSocketHandler.parseMessage("trx_count:1;");
    tciWebSocketHandler.parseMessage("iq_start:0;");
    assertTrue(tciTrxState.getTrx().get(0).isIQEnabled());
    tciWebSocketHandler.parseMessage("iq_stop:0;");
    assertFalse(tciTrxState.getTrx().get(0).isIQEnabled());
  }

  @Test
  void parseRxTxEnable() {
    tciWebSocketHandler.parseMessage("trx_count:1;");
    tciWebSocketHandler.parseMessage("rx_enable:0,false;");
    tciWebSocketHandler.parseMessage("tx_enable:0,true;");
    assertFalse(tciTrxState.getTrx().get(0).isRxEnabled());
    assertTrue(tciTrxState.getTrx().get(0).isTxEnabled());
  }

  @Test
  void parseInvalidCommand() {
    boolean result = tciTrxState.parseCommand("unknown_cmd", new String[]{"foo"});
    assertFalse(result);
  }

  @Test
  void parseMalformedParams() {
    assertThrows(IndexOutOfBoundsException.class, () -> {
      tciTrxState.parseCommand("dds", new String[]{"0"});
    });
  }
}
