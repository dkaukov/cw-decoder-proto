package io.github.dkaukov.cw_decoder_proto.fx;


import net.rgielen.fxweaver.core.FxWeaver;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.github.dkaukov.cw_decoder_proto.atoms.StageReadyEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.SneakyThrows;

@Component
public class FXInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    public FXInitializer(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        Stage stage = stageReadyEvent.getStage();
        Scene scene = new Scene(fxWeaver.loadView(MainController.class));
        stage.setScene(scene);
        stage.show();
    }
}
