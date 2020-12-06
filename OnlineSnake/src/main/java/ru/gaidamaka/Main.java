package ru.gaidamaka;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import ru.gaidamaka.config.Config;
import ru.gaidamaka.config.ConfigReader;
import ru.gaidamaka.gui.GameWindowController;
import ru.gaidamaka.gui.MoveHandler;

import java.io.IOException;


public class Main extends Application {
    private static final String CONFIG_PATH = "config.properties";
    private static final String GAME_VIEW_FXML_PATH = "GameView.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Config config = ConfigReader.readProtoConfig(CONFIG_PATH);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getClassLoader().getResource(GAME_VIEW_FXML_PATH));
            SplitPane root = loader.load();
            GameWindowController controller = loader.getController();
            MoveHandler moveHandler = new MoveHandler(config, null);
            moveHandler.setView(controller);
            controller.setStage(stage);
            controller.setGamePresenter(moveHandler);
            stage.setScene(new Scene(root));
            stage.sizeToScene();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
