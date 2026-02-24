package io.github.dkaukov.tci;

import java.util.Objects;
import java.util.OptionalInt;

public final class TCICommandSerializer {

  private TCICommandSerializer() {
  }

  public sealed interface Command permits Tune, CwMessage, Start, Stop, RxSmeter, Drive, TuneDrive {
  }

  public record Tune(int trx, boolean enabled) implements Command {
  }

  public record CwMessage(int trx, String preCallsign, String callsign, String postCallsign)
      implements Command {
  }

  public record Start() implements Command {
  }

  public record Stop() implements Command {
  }

  public record RxSmeter(int trx, int channel) implements Command {
  }

  public record Drive(OptionalInt trx, float power) implements Command {
    public Drive {
      Objects.requireNonNull(trx, "trx must not be null");
    }

    public static Drive global(float power) {
      return new Drive(OptionalInt.empty(), power);
    }

    public static Drive perTrx(int trx, float power) {
      return new Drive(OptionalInt.of(trx), power);
    }
  }

  public record TuneDrive(OptionalInt trx, float power) implements Command {
    public TuneDrive {
      Objects.requireNonNull(trx, "trx must not be null");
    }

    public static TuneDrive global(float power) {
      return new TuneDrive(OptionalInt.empty(), power);
    }

    public static TuneDrive perTrx(int trx, float power) {
      return new TuneDrive(OptionalInt.of(trx), power);
    }
  }

  public static String serialize(Command command) {
    if (command instanceof Tune c) {
      return "tune:" + c.trx() + "," + c.enabled() + ";";
    }
    if (command instanceof CwMessage c) {
      return "cw_msg:" + c.trx() + "," + c.preCallsign() + "," + c.callsign() + "," + c.postCallsign()
          + ";";
    }
    if (command instanceof Start) {
      return "start;";
    }
    if (command instanceof Stop) {
      return "stop;";
    }
    if (command instanceof RxSmeter c) {
      return "rx_smeter:" + c.trx() + "," + c.channel() + ";";
    }
    if (command instanceof Drive c) {
      return c.trx().isPresent()
          ? "drive:" + c.trx().getAsInt() + "," + formatNumber(c.power()) + ";"
          : "drive:" + formatNumber(c.power()) + ";";
    }
    if (command instanceof TuneDrive c) {
      return c.trx().isPresent()
          ? "tune_drive:" + c.trx().getAsInt() + "," + formatNumber(c.power()) + ";"
          : "tune_drive:" + formatNumber(c.power()) + ";";
    }
    throw new IllegalArgumentException("Unsupported command type: " + command.getClass().getName());
  }

  private static String formatNumber(float value) {
    if (value == (long) value) {
      return Long.toString((long) value);
    }
    return Float.toString(value);
  }
}
