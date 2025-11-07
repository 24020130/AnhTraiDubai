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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Utlis.SoundManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {
    private final Pane root;
    private Paddle paddle;
    private Ball ball;
    private List<Ball> balls = new ArrayList<>();
    private Level level;
    private final List<PowerUp> powerUps = new ArrayList<>();
    private AnimationTimer timer;
    private int currentLevel = 1;
    private int playerScore = 0;
    private int playerLives = 4;
    private Image bgImage;
    private ImageView bg1, bg2;
    private double bgSpeed = 0.2;
    private final List<Meteor> meteors = new ArrayList<>();
    private long lastMeteorSpawn = 0;

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
        balls.clear();



        switch (currentLevel) {
            case 1: bgImage = ImageLoader.BACKGROUND_LEVEL1; break;
            case 2: bgImage = ImageLoader.BACKGROUND_LEVEL2; break;
            case 3: bgImage = ImageLoader.BACKGROUND_LEVEL3; break;
            case 4: bgImage = ImageLoader.BACKGROUND_LEVEL4; break;
            default: bgImage = ImageLoader.BACKGROUND_LEVEL1; break;
        }

        bg1 = new ImageView(bgImage);
        bg2 = new ImageView(bgImage);

        bg1.setFitWidth(Config.WINDOW_WIDTH - 220);
        bg1.setFitHeight(Config.WINDOW_HEIGHT);
        bg1.setPreserveRatio(false);

        bg2.setFitWidth(Config.WINDOW_WIDTH - 220);
        bg2.setFitHeight(Config.WINDOW_HEIGHT);
        bg2.setPreserveRatio(false);

// đặt bg2 ngay sau bg1
        bg2.setLayoutX(bg1.getFitWidth());

        root.getChildren().addAll(bg1, bg2);

        paddle = new Paddle(350, 650, 100, 20, Config.PADDLE_SPEED, this);
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

        List<Ball> allBalls = new ArrayList<>();
        allBalls.add(ball);
        allBalls.addAll(balls);

        List<PowerUp> newPowerUps = new ArrayList<>();
        for (Ball b : allBalls) {
            b.update();
            b.checkCollisionWithWalls(Config.WINDOW_WIDTH - 220, Config.WINDOW_HEIGHT);
            if (!b.hasCollidedWithPaddle() && CollisionHandler.checkCollision(b, paddle)) {
                CollisionHandler.handleBallPaddleCollision(b, paddle);
                SoundManager.playEffect("collision2.mp3");
                b.setHasCollidedWithPaddle(true);
            } else if (!CollisionHandler.checkCollision(b, paddle)) {
                // reset cờ khi ball không còn chạm paddle
                b.setHasCollidedWithPaddle(false);
            }
            Iterator<Brick> iterator = level.getBricks().iterator();
            while (iterator.hasNext()) {
                Brick brick = iterator.next();
                if (CollisionHandler.handleBallBrickCollision(b, brick, newPowerUps, root)) {
                    SoundManager.playEffect("collision.mp3");
                    if (brick.isDestroyed()) {
                        iterator.remove();
                        playerScore += 10;
                    }
                    break;
                }
            }
        }

        for (PowerUp p : newPowerUps) {
            addPowerUp(p);
        }


        CollisionHandler.handlePowerUpCollision(powerUps, paddle, root);

        balls.removeIf(b -> {
            if (b.getY() > Config.WINDOW_HEIGHT) {
                root.getChildren().remove(b.getView());
                return true;
            }
            return false;
        });


        if (level.getBricks().isEmpty()) nextLevel();

        if (ball.getY() > Config.WINDOW_HEIGHT && balls.isEmpty()) {
            if (playerLives > 0) {
                playerLives--;
                restartLevel();
            } else {
                org.example.baitaplamgame.Utlis.ScoreFileManager.saveScore("player1", playerScore, currentLevel);
                restartLevel();
            }
        }

        hudPanel.updateHUD(currentLevel, playerScore, playerLives);
        // Background scrolling effect
        bg1.setLayoutX(bg1.getLayoutX() - bgSpeed);
        bg2.setLayoutX(bg2.getLayoutX() - bgSpeed);

        if (bg1.getLayoutX() + bg1.getFitWidth() <= 0) {
            bg1.setLayoutX(bg2.getLayoutX() + bg2.getFitWidth());
        }
        if (bg2.getLayoutX() + bg2.getFitWidth() <= 0) {
            bg2.setLayoutX(bg1.getLayoutX() + bg1.getFitWidth());
        }
        // === Meteor logic ===
        long currentTime = System.nanoTime();
        if (currentTime - lastMeteorSpawn > 5_000_000_000L) { // mỗi 5s spawn 1 viên
            double x = Math.random() * (Config.WINDOW_WIDTH - 260);
            Meteor m = new Meteor(x, -50);
            meteors.add(m);
            root.getChildren().add(m);
            lastMeteorSpawn = currentTime;
        }

// update và check va chạm
        Iterator<Meteor> mIter = meteors.iterator();
        while (mIter.hasNext()) {
            Meteor m = mIter.next();
            m.update(root);

            if (!m.isActive()) {
                mIter.remove();
                continue;
            }

            // va chạm với paddle
            if (m.getBoundsInParent().intersects(paddle.getView().getBoundsInParent())) {
                m.destroy(root);
                playerLives--;
                mIter.remove();
                continue;
            }


        }

    }

    public void spawnExtraBalls(Ball sourceBall, int count) {
        for (int i = 0; i < count; i++) {
            Ball newBall = new Ball(
                    sourceBall.getX(),
                    sourceBall.getY(),
                    sourceBall.getView().getFitWidth(),
                    sourceBall.getSpeed()
            );
            double vx = sourceBall.getVelocityX() + (Math.random() - 0.5) * 4;
            double vy = -Math.abs(sourceBall.getVelocityY());
            newBall.setVelocity(vx, vy);
            balls.add(newBall);
            root.getChildren().add(newBall.getView());
        }
    }

    private void nextLevel() {
        currentLevel++;
        timer.stop();

        javafx.scene.image.Image nextLevelImageSource = new javafx.scene.image.Image(getClass().getResourceAsStream("/images/next.png"));
        javafx.scene.image.ImageView nextLevelImage = new javafx.scene.image.ImageView(nextLevelImageSource);

        nextLevelImage.setFitWidth(500);
        nextLevelImage.setFitHeight(200);
        nextLevelImage.setPreserveRatio(false);
        nextLevelImage.setLayoutX((Config.WINDOW_WIDTH  - 220 - nextLevelImage.getFitWidth()) / 2);
        nextLevelImage.setLayoutY((Config.WINDOW_HEIGHT - nextLevelImage.getFitHeight()) / 2);

        nextLevelImage.setOpacity(0);
        root.getChildren().add(nextLevelImage);

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

    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
        if (!root.getChildren().contains(powerUp.getView())) {
            root.getChildren().add(powerUp.getView());
        }
    }
}
