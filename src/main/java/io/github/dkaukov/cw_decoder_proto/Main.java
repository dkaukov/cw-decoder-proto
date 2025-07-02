package io.github.dkaukov.cw_decoder_proto;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.dkaukov.cw_decoder_proto.fx.FXMain;
import javafx.application.Application;

@SpringBootApplication
public class Main {

  public static void main(String[] args) {
    Application.launch(FXMain.class, args);
  }

}
