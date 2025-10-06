package org.example.baitaplamgame.Model;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Level.Level;
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
        root.getChildren().clear();
        powerUps.clear();

        javafx.scene.image.ImageView bgView = new javafx.scene.image.ImageView(ImageLoader.BACKGROUND_IMAGE);
        bgView.setFitWidth(Config.WINDOW_WIDTH);
        bgView.setFitHeight(Config.WINDOW_HEIGHT);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        paddle = new Paddle(350, 650, 100, 20, Config.PADDLE_SPEED);
        ball = new Ball(390, 500, 20, Config.BALL_SPEED);

        level = new Level(currentLevel);
        level.generateLevel(currentLevel, root);

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



        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp p = powerUpIterator.next();
            p.update();


            if (CollisionHandler.checkCollision(p, paddle) && !p.isCollected()) {
                p.applyEffect(paddle);
                p.setCollected(true);
                root.getChildren().remove(p.getView());
                powerUpIterator.remove();
            }
        }


        if (level.getBricks().isEmpty()) {
            nextLevel();
        }

        if (ball.getY() > Config.WINDOW_HEIGHT) {
            restartLevel();
        }
    }

    private void nextLevel() {
        currentLevel++;
        startGame();
    }

    private void restartLevel() {
        System.out.println("Bóng rơi! Reset lại level " + currentLevel);
        startGame();
    }

    public void setupInput(Scene scene) {
        scene.setOnKeyPressed(e -> InputKeys.setKeyPressed(e.getCode().toString()));
        scene.setOnKeyReleased(e -> InputKeys.setKeyReleased(e.getCode().toString()));
    }
}
