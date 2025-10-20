package org.example.baitaplamgame.Model;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Level.Level;
import org.example.baitaplamgame.Level.Level1;
import org.example.baitaplamgame.Level.Level2;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.InputKeys;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {
    private final Pane root;
    private Paddle paddle;
    private Ball ball;
    private Level level;
    private final List<PowerUp> powerUps = new ArrayList<>();
    private AnimationTimer timer;
    private int currentLevel = 1;
    private int playerScore = 0;
    private int playerLives = 4;
    private org.example.baitaplamgame.Ui.HUDPanel hudPanel;


    public GameManager(Pane root, double width, double height) {
        this.root = root;
    }

    public void startGame() {
        currentLevel = 1;
        startLevel1();
    }

    private void startLevel1() {
        startLevel(new Level1(1), "/levels/level1.txt");
    }

    private void startLevel2() {
        startLevel(new Level2(2), "/levels/level2.txt");
    }

    private void startLevel(Level levelObj, String filePath) {
        root.getChildren().clear();
        powerUps.clear();

        var bgView = new javafx.scene.image.ImageView(ImageLoader.BACKGROUND_IMAGE);
        bgView.setFitWidth(Config.WINDOW_WIDTH - 220);
        bgView.setFitHeight(Config.WINDOW_HEIGHT);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        paddle = new Paddle(350, 650, 100, 20, Config.PADDLE_SPEED);
        ball = new Ball(390, 500, 20, Config.BALL_SPEED);
        paddle.setBall(ball);
        this.level = levelObj;
        level.generateLevelFromFile(filePath, root);

        root.getChildren().addAll(paddle.getView(), ball.getView());
        hudPanel = new org.example.baitaplamgame.Ui.HUDPanel(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        root.getChildren().add(hudPanel);
        hudPanel.slideIn();


        if (timer != null) timer.stop();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }

    public void update() {
        if (InputKeys.isPressed("LEFT")) paddle.moveLeft();
        if (InputKeys.isPressed("RIGHT")) paddle.moveRight();

        ball.update();
        ball.checkCollisionWithWalls(Config.WINDOW_WIDTH - 220, Config.WINDOW_HEIGHT);
        CollisionHandler.handleBallPaddleCollision(ball, paddle);

        List<PowerUp> newPowerUps = new ArrayList<>();
        Iterator<Brick> iterator = level.getBricks().iterator();
        while (iterator.hasNext()) {
            Brick b = iterator.next();
            if (CollisionHandler.handleBallBrickCollision(ball, b, newPowerUps, root)) {
                if(b.isDestroyed()) {
                    iterator.remove();
                    playerScore += 10;
                }
            }
        }
        for (PowerUp p : newPowerUps) {
            powerUps.add(p);
            if (!root.getChildren().contains(p.getView())) {
                root.getChildren().add(p.getView());
            }
        }
        CollisionHandler.handlePowerUpCollision(powerUps, paddle, root);
        if (level.getBricks().isEmpty()) nextLevel();
        if (ball.getY() > Config.WINDOW_HEIGHT) {
            if(playerLives > 0) {
                playerLives--;
            }
            else {
                org.example.baitaplamgame.Utlis.ScoreFileManager.saveScore("player1", playerScore, currentLevel);
                restartLevel();
            }
        }
        hudPanel.updateHUD(currentLevel, playerScore, playerLives);

    }

    private void nextLevel() {
        currentLevel++;
        timer.stop();

        String imagePath = "C:\\Users\\Lenovo LOQ\\Videos\\ProjectGame\\next.png"; // hoặc "C:/GameProject/assets/next_level.png"
        javafx.scene.image.ImageView nextLevelImage = new javafx.scene.image.ImageView(new javafx.scene.image.Image("file:" + imagePath));

        nextLevelImage.setFitWidth(500);
        nextLevelImage.setPreserveRatio(true);
        nextLevelImage.setLayoutX(Config.WINDOW_WIDTH / 2 - 250);
        nextLevelImage.setLayoutY(Config.WINDOW_HEIGHT / 2 - 150);
        nextLevelImage.setOpacity(0);
        root.getChildren().add(nextLevelImage);

        // Hiệu ứng
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.8), nextLevelImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));

        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.2), nextLevelImage);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        javafx.animation.SequentialTransition seq = new javafx.animation.SequentialTransition(fadeIn, pause, fadeOut);
        seq.setOnFinished(e -> {
            root.getChildren().remove(nextLevelImage);
            if (currentLevel == 2) {
                startLevel2();
            } else {
                System.out.println("Hoàn thành toàn bộ level!");
            }
        });
        seq.play();
    }



    private void restartLevel() {
        System.out.println("Bóng rơi! Reset lại level " + currentLevel);
        if (currentLevel == 1)
            startLevel1();
        else if (currentLevel == 2)
            startLevel2();
    }

    public void setupInput(Scene scene) {
        scene.setOnKeyPressed(e -> InputKeys.setKeyPressed(e.getCode().toString()));
        scene.setOnKeyReleased(e -> InputKeys.setKeyReleased(e.getCode().toString()));
    }

//    private void showGameOverScreen() {
//        timer.stop();
//        root.getChildren().clear();
//
//        javafx.scene.image.ImageView bg = new javafx.scene.image.ImageView(ImageLoader.BACKGROUND_IMAGE);
//        bg.setFitWidth(Config.WINDOW_WIDTH);
//        bg.setFitHeight(Config.WINDOW_HEIGHT);
//        root.getChildren().add(bg);
//
//        javafx.scene.control.Label gameOverLabel = new javafx.scene.control.Label("GAME OVER");
//        gameOverLabel.setStyle("-fx-font-size: 72px; -fx-text-fill: red; -fx-font-weight: bold;");
//        gameOverLabel.setLayoutX(Config.WINDOW_WIDTH / 2 - 200);
//        gameOverLabel.setLayoutY(Config.WINDOW_HEIGHT / 2 - 150);
//        gameOverLabel.setOpacity(0);
//
//        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1), gameOverLabel);
//        fadeIn.setFromValue(0);
//        fadeIn.setToValue(1);
//        fadeIn.play();
//
//        javafx.scene.control.Button restartButton = new javafx.scene.control.Button("Restart");
//        restartButton.setStyle("-fx-font-size: 24px; -fx-background-color: #0f0; -fx-text-fill: black;");
//        restartButton.setLayoutX(Config.WINDOW_WIDTH / 2 - 80);
//        restartButton.setLayoutY(Config.WINDOW_HEIGHT / 2 + 50);
//        // Hiệu ứng phóng to nhẹ khi hiện nút Restart
//        javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(javafx.util.Duration.seconds(0.8), restartButton);
//        scale.setFromX(0);
//        scale.setFromY(0);
//        scale.setToX(1);
//        scale.setToY(1);
//        scale.play();
//
//        restartButton.setOnAction(e -> {
//            playerLives = 3;
//            playerScore = 0;
//            currentLevel = 1;
//            startLevel1();
//        });
//
//        root.getChildren().addAll(gameOverLabel, restartButton);
//    }
}