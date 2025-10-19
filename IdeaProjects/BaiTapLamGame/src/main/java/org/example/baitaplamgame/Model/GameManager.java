package org.example.baitaplamgame.Model;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Level.*;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.InputKeys;

import java.lang.reflect.Array;
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

    private void startLevel3() {
        startLevel(new Level3(3), "/levels/level3.txt");
    }

    private void startLevel4() {
        startLevel(new Level4(4), "/levels/level4.txt");
    }

    private void startLevel(Level levelObj, String filePath) {
        root.getChildren().clear();
        powerUps.clear();

        var bgView = new javafx.scene.image.ImageView(ImageLoader.BACKGROUND_IMAGE);
        bgView.setFitWidth(Config.WINDOW_WIDTH);
        bgView.setFitHeight(Config.WINDOW_HEIGHT);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        paddle = new Paddle(350, 650, 100, 20, Config.PADDLE_SPEED,this);
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
        // Di chuyển paddle
        if (InputKeys.isPressed("LEFT")) paddle.moveLeft();
        if (InputKeys.isPressed("RIGHT")) paddle.moveRight();

        // Gom tất cả bóng: bóng gốc + bóng được sinh thêm
        List<Ball> allBalls = new ArrayList<>();
        allBalls.add(ball);
        allBalls.addAll(balls);

        // Danh sách PowerUp mới sinh ra khi đập gạch
        List<PowerUp> newPowerUps = new ArrayList<>();

        // Duyệt qua tất cả bóng
        for (Ball b : allBalls) {
            b.update();
            b.checkCollisionWithWalls(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            CollisionHandler.handleBallPaddleCollision(b, paddle);

            // Va chạm bóng với gạch
            Iterator<Brick> iterator = level.getBricks().iterator();
            while (iterator.hasNext()) {
                Brick brick = iterator.next();
                if (CollisionHandler.handleBallBrickCollision(ball, brick, newPowerUps, root)) {
                    iterator.remove();
                }
            }
        }

        // Thêm các power-up mới vào danh sách và scene
        for (PowerUp p : newPowerUps) {
            powerUps.add(p);
            if (!root.getChildren().contains(p.getView())) {
                root.getChildren().add(p.getView());
            }
        }

        // Xử lý va chạm giữa paddle và power-up
        CollisionHandler.handlePowerUpCollision(powerUps, paddle, root);

        // Dọn các bóng rơi khỏi màn hình (rơi xuống dưới)
        balls.removeIf(b -> {
            if (b.getY() > Config.WINDOW_HEIGHT) {
                root.getChildren().remove(b.getView());
                return true;
            }
            return false;
        });

        // Kiểm tra qua màn / thua
        if (level.getBricks().isEmpty()) nextLevel();
        if (ball.getY() > Config.WINDOW_HEIGHT && balls.isEmpty()) restartLevel();
    }

    public void spawnExtraBalls(Ball sourceBall, int count) {
        for (int i = 0; i < count; i++) {
            Ball newBall = new Ball(
                    sourceBall.getX(),
                    sourceBall.getY(),
                    sourceBall.getView().getFitWidth(),
                    sourceBall.getSpeed()
            );

            // Cho hướng ngẫu nhiên một chút
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
            }
            else if(currentLevel == 3){
                startLevel3();
            }
            else {
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
        else if (currentLevel == 3){
            startLevel3();
        }
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

