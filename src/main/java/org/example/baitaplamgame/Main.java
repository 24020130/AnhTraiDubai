package org.example.baitaplamgame;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
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
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.SoundManager;

import java.util.*;

import javafx.scene.layout.StackPane;
// 💡 Thêm import cho Client và Server
import org.example.baitaplamgame.Network.Client;
import org.example.baitaplamgame.Network.Server;

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
        String videoPath = getClass().getResource("/images/intro11.mp4").toExternalForm();
        Media media = new Media(videoPath);
        MediaPlayer player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);

        mediaView.setFitWidth(width);
        mediaView.setFitHeight(height);
        mediaView.setPreserveRatio(false);

        StackPane introRoot = new StackPane(mediaView);
        Scene introScene = new Scene(introRoot, width, height);

        stage.setScene(introScene);
        stage.setTitle("Brick Breaker - Intro");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
        stage.show();

        player.play();

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

        // 🔥🔥🔥 BỔ SUNG QUAN TRỌNG: Gán callback Multiplayer 🔥🔥🔥
        menu.setOnStartServer(port -> startServerGame(port));
        menu.setOnStartClient(ip -> startClientGame(ip, 5000));

        Scene menuScene = new Scene(menu, width, height);
        SoundManager.playBackground("background.mp3");
        stage.setScene(menuScene);
        stage.setTitle("Brick Breaker - Menu");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();

        if (!stage.isShowing()) stage.show();

        // Khi người chơi nhấn Start trong menu (Singleplayer)
        menu.setOnStart(() -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), menu);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> startGame());
            fadeOut.play();
        });
    }

    // =========================
    // 🕹️ SCENE GAME (Singleplayer)
    // =========================
    private void startGame() {
        Pane gameRoot = new Pane();
        GameManager gameManager = new GameManager(gameRoot, width, height);

        // [1] Nút EXIT nhanh (gọi onExitToMenu)
        gameManager.setOnExitToMenu(this::showMainMenu);

        // [2] Game Over/Thua (gọi onGameOver)
        gameManager.setOnGameOver(this::showMainMenu);

        // [3] Kết thúc game (WIN/Multiplayer End) (gọi onGameEndToMenu)
        gameManager.setOnGameEndToMenu(this::showMainMenu);

        Scene gameScene = new Scene(gameRoot, width, height);

        gameManager.setupInput(gameScene);

        stage.setScene(gameScene);
        stage.setTitle("Brick Breaker - Playing");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();

        gameRoot.requestFocus();
        gameManager.startGame();
    }

    // =========================
    // 🌐 CLIENT GAME (Multiplayer)
    // =========================
    public void startClientGame(String serverIp, int port) {
        Client client = new Client();

        // 🔥 BỔ SUNG: Ẩn Stage Menu chỉ khi Game đã mở
        client.setOnGameStart(() -> Platform.runLater(() -> stage.hide()));

        client.setOnGameEndToMenu(() -> {
            Platform.runLater(this::showMainMenu);
        });

        client.connect(serverIp, port);
    }


    // =========================
    // 🏠 SERVER GAME (Multiplayer)
    // =========================
    public void startServerGame(int port) {
        Server server = new Server();

        // 🔥 BỔ SUNG: Ẩn Stage Menu chỉ khi Game đã mở
        server.setOnGameStart(() -> Platform.runLater(() -> stage.hide()));

        server.setOnGameEndToMenu(() -> {
            Platform.runLater(this::showMainMenu);
        });

        server.startServer(port);
    }


    public static void main(String[] args) {
        launch();
    }
}