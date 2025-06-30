package io.github.dkaukov.cw_decoder_proto;


import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import io.github.dkaukov.cw_decoder_proto.atoms.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FXMain extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void start(Stage stage) {
        context.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void init() throws Exception {
        super.init();
        context = new SpringApplicationBuilder(Main.class).run();
    }

    @Override
    public void stop() throws Exception {
        context.close();
        super.stop();
        Platform.exit();
    }
}
