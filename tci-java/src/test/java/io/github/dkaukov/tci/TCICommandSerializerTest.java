package io.github.dkaukov.tci;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TCICommandSerializerTest {

  @Test
  void serializeTune() {
    assertEquals("tune:0,true;",
        TCICommandSerializer.serialize(new TCICommandSerializer.Tune(0, true)));
  }

  @Test
  void serializeCwMessage() {
    assertEquals("cw_msg:0,TEST1,TEST2,TEST3;",
        TCICommandSerializer.serialize(
            new TCICommandSerializer.CwMessage(0, "TEST1", "TEST2", "TEST3")));
  }

  @Test
  void serializeStartStop() {
    assertEquals("start;", TCICommandSerializer.serialize(new TCICommandSerializer.Start()));
    assertEquals("stop;", TCICommandSerializer.serialize(new TCICommandSerializer.Stop()));
  }

  @Test
  void serializeRxSmeter() {
    assertEquals("rx_smeter:1,2;",
        TCICommandSerializer.serialize(new TCICommandSerializer.RxSmeter(1, 2)));
  }

  @Test
  void serializeDrive() {
    assertEquals("drive:10;",
        TCICommandSerializer.serialize(TCICommandSerializer.Drive.global(10.0f)));
    assertEquals("drive:1,10;",
        TCICommandSerializer.serialize(TCICommandSerializer.Drive.perTrx(1, 10.0f)));
  }

  @Test
  void serializeTuneDrive() {
    assertEquals("tune_drive:10;",
        TCICommandSerializer.serialize(TCICommandSerializer.TuneDrive.global(10.0f)));
    assertEquals("tune_drive:0,10;",
        TCICommandSerializer.serialize(TCICommandSerializer.TuneDrive.perTrx(0, 10.0f)));
  }
}
