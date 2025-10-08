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
        if (currentLevel == 2) {
            startLevel2();
        } else {
            System.out.println("Bạn đã hoàn thành tất cả level!");
            timer.stop();
        }
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
