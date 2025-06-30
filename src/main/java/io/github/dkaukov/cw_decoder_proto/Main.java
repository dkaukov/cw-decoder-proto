package io.github.dkaukov.cw_decoder_proto;

import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javafx.application.Application;

@SpringBootApplication
public class Main {

  public static void main(String[] args) {
    Application.launch(FXMain.class, args);
  }

  @Bean
  public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
    // Would also work with javafx-weaver-core only:
    // return new FxWeaver(applicationContext::getBean, applicationContext::close);
    return new SpringFxWeaver(applicationContext);
  }

}
