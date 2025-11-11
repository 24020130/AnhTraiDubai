package org.example.baitaplamgame;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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

        // =========================
        // 🎬 SCENE INTRO VIDEO
        // =========================
        String videoPath = getClass().getResource("/images/intro2.mp4").toExternalForm();
        Media media = new Media(videoPath);
        MediaPlayer player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);

        // ✅ Dùng kích thước theo Config
        mediaView.setFitWidth(width);
        mediaView.setFitHeight(height);

        Pane introRoot = new Pane(mediaView);
        Scene introScene = new Scene(introRoot, width, height);

        stage.setScene(introScene);
        stage.setTitle("Brick Breaker - Intro");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
        stage.show();

        // 🔊 Phát video intro
        player.play();

        // Khi video kết thúc -> chuyển sang menu
        player.setOnEndOfMedia(() -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), introRoot);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> showMainMenu());
            fadeOut.play();
        });
    }

    // =========================
    // 🎮 MENU GAME
    // =========================
    private void showMainMenu() {
        GamePanel menu = new GamePanel();
        menu.setPrefSize(width, height);
        Scene menuScene = new Scene(menu, width, height);

        stage.setScene(menuScene);
        stage.setTitle("Brick Breaker - Menu");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
        stage.show();

        // Khi người chơi nhấn Start trong menu
        menu.setOnStart(() -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), menu);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> startGame());
            fadeOut.play();
        });
    }

    // =========================
    // 🕹️ SCENE GAME
    // =========================
    private void startGame() {
        Pane gameRoot = new Pane();
        GameManager gameManager = new GameManager(gameRoot, width, height);
        gameManager.setOnExitToMenu(this::showMainMenu);

        Scene gameScene = new Scene(gameRoot, width, height);

        // GỌI INPUT TRƯỚC
        gameManager.setupInput(gameScene);

        stage.setScene(gameScene);
        stage.setTitle("Brick Breaker - Playing");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();

        // ✅ Focus và khởi động game
        gameRoot.requestFocus();
        gameManager.startGame();
    }

    public static void main(String[] args) {
        launch();
    }
}
