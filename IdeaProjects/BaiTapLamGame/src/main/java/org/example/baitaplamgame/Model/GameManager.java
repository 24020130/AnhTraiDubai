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
        bgView.setFitWidth(Config.WINDOW_WIDTH);
        bgView.setFitHeight(Config.WINDOW_HEIGHT);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        paddle = new Paddle(350, 650, 100, 20, Config.PADDLE_SPEED);
        ball = new Ball(390, 500, 20, Config.BALL_SPEED);
        paddle.setBall(ball);
        this.level = levelObj;
        level.generateLevelFromFile(filePath, root);

        root.getChildren().addAll(paddle.getView(), ball.getView());

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
        ball.checkCollisionWithWalls(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        CollisionHandler.handleBallPaddleCollision(ball, paddle);

        List<PowerUp> newPowerUps = new ArrayList<>();
        Iterator<Brick> iterator = level.getBricks().iterator();
        while (iterator.hasNext()) {
            Brick b = iterator.next();
            if (CollisionHandler.handleBallBrickCollision(ball, b, newPowerUps, root)) {
                iterator.remove();
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
        if (ball.getY() > Config.WINDOW_HEIGHT) restartLevel();
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
}
