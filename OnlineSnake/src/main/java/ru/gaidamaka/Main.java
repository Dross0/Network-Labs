package ru.gaidamaka;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import ru.gaidamaka.config.ProtoGameConfigAdapter;
import ru.gaidamaka.game.Game;
import ru.gaidamaka.gui.GameWindowController;
import ru.gaidamaka.gui.MoveHandler;

import java.io.IOException;


public class Main extends Application {
    private Stage primaryStage;
    private Scene menuScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SnakesProto.GameConfig config = SnakesProto.GameConfig.newBuilder()
                .setDeadFoodProb(1.0f)
                .setHeight(10)
                .setWidth(10)
                .setFoodStatic(3)
                .setFoodPerPlayer(1)
                .build();
        this.primaryStage = stage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getClassLoader().getResource("GameView.fxml"));
            SplitPane root = loader.load();
            GameWindowController controller = loader.getController();
            ProtoGameConfigAdapter configAdapter = new ProtoGameConfigAdapter(config);
            Game game = new Game(configAdapter);
            controller.setGameConfig(configAdapter);
            MoveHandler moveHandler = new MoveHandler(250);
            moveHandler.setGame(game);
            moveHandler.setView(controller);
            controller.setStage(stage);
            controller.setGamePresenter(moveHandler);
            controller.builtField();
            moveHandler.start();
            this.menuScene = new Scene(root);
            stage.setScene(this.menuScene);
            stage.sizeToScene();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
