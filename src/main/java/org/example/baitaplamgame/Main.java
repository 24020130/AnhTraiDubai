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

    private Stage stage; // để dùng lại khi quay về menu
    private final double width = Config.WINDOW_WIDTH;
    private final double height = Config.WINDOW_HEIGHT;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        showMainMenu();
    }

    private void showMainMenu() {
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
            fadeOut.setOnFinished(event -> startGame());
            fadeOut.play();
        });
    }

    private void startGame() {
        Pane gameRoot = new Pane();
        GameManager gameManager = new GameManager(gameRoot, width, height);
        gameManager.setOnExitToMenu(this::showMainMenu);

        Scene gameScene = new Scene(gameRoot, width, height);

        // GỌI INPUT TRƯỚC
        gameManager.setupInput(gameScene);

        // Đặt Scene lên Stage
        stage.setScene(gameScene);
        stage.setTitle("Brick Breaker - Playing");

        // ✅ Gọi focus đúng cách
        gameRoot.requestFocus();

        // Bắt đầu game
        gameManager.startGame();
    }

    public static void main(String[] args) {
        launch();
    }
}
