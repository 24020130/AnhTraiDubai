package org.example.baitaplamgame;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.baitaplamgame.Model.GameManager;
import org.example.baitaplamgame.Ui.GamePanel;
import org.example.baitaplamgame.Utlis.Config;

public class Main extends Application {
    @Override
    public void





    start(Stage stage) {
        double width = Config.WINDOW_WIDTH;
        double height = Config.WINDOW_HEIGHT;
        GamePanel menu = new GamePanel();
        menu.setPrefSize(width, height);
        Scene menuScene = new Scene(menu, width, height);

        stage.setScene(menuScene);
        stage.setTitle("Brick Breaker - Menu");
        stage.show();
        menu.setOnStart(() -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), menu);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> {
                Pane gameRoot = new Pane();
                GameManager gameManager = new GameManager(gameRoot, width, height);
                gameManager.startGame();
                Scene gameScene = new Scene(gameRoot, width, height);
                gameManager.setupInput(gameScene);
                stage.setScene(gameScene);
                stage.setTitle("Brick Breaker - Playing");
            });
            fadeOut.play();
        });

    }

    public static void main(String[] args) {
        launch();
    }
}
