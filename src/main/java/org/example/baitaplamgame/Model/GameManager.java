package org.example.baitaplamgame.Model;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Level.Level;
import org.example.baitaplamgame.Level.Level1;
import org.example.baitaplamgame.Level.Level2;
import org.example.baitaplamgame.Level.Level5;
import org.example.baitaplamgame.PowerUp.BossFireBall;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.InputKeys;
import org.example.baitaplamgame.PowerUp.BossBullet;

import javafx.scene.image.ImageView;
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
    private final List<BossFireBall> bossFireBalls = new ArrayList<>();
    private long lastFireballTime = 0;
    private AnimationTimer timer;
    private int currentLevel = 1;
    private int playerScore = 0;
    private int playerLives = 4;
    private boolean gameOver = false;
    private Runnable onExitToMenu;
    private org.example.baitaplamgame.Ui.HUDPanel hudPanel;
    private java.io.BufferedWriter writer;
    public void setWriter(java.io.BufferedWriter w) { this.writer = w; }
    public void setOnExitToMenu(Runnable callback) {
        this.onExitToMenu = callback;
    }
//    public void startGame(int level) {
//        this.currentLevel = level; // nếu bạn có biến currentLevel
//        startGame(); // gọi lại hàm gốc
//    }



    public GameManager(Pane root, double width, double height) {
        this.root = root;
    }
    public void startGame() {
        currentLevel = 1;
        playerLives = 4;
        playerScore = 0;
        gameOver = false;

        switch (currentLevel) {
            case 1 -> startLevel1();
            case 2 -> startLevel2();
            case 3, 4, 5 -> startLevel5();
            default -> startLevel1();
        }
    }

    private void startLevel1() {
        startLevel(new Level1(1), "/levels/level1.txt");
    }

    private void startLevel2() {
        startLevel(new Level2(2), "/levels/level2.txt");
    }

    private void startLevel5() {
        startLevel(new Level5(5), "/levels/level5.txt");
    }

    private void startLevel(Level levelObj, String filePath) {
        gameOver = false;

        root.getChildren().clear();
        powerUps.clear();
        balls.clear();

        var bgImage = switch (currentLevel) {
            case 1 -> ImageLoader.BACKGROUND_LEVEL1;
            case 2 -> ImageLoader.BACKGROUND_LEVEL2;
            case 3 -> ImageLoader.BACKGROUND_LEVEL3;
            case 4 -> ImageLoader.BACKGROUND_LEVEL4;
            default -> ImageLoader.BACKGROUND_LEVEL1;
        };

        var bgView = new ImageView(bgImage);
        bgView.setFitWidth(Config.WINDOW_WIDTH - 220);
        bgView.setFitHeight(Config.WINDOW_HEIGHT);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        paddle = new Paddle(350, 650, 100, 20, Config.PADDLE_SPEED, this);
        ball = new Ball(390, 500, 20, Config.BALL_SPEED);
        paddle.setBall(ball);
        this.level = levelObj;
        level.generateLevelFromFile(filePath, root);

        root.getChildren().addAll(paddle.getView(), ball.getView());
        hudPanel = new org.example.baitaplamgame.Ui.HUDPanel(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        hudPanel.setOnSave(() -> {
            org.example.baitaplamgame.Utlis.ScoreFileManager.saveScore(
                    Config.PLAYER_NAME, playerScore, currentLevel
            );
        });

        hudPanel.setOnExit(() -> {
            if (timer != null) timer.stop();
            if (onExitToMenu != null) {
                onExitToMenu.run();
            }
        });

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
        if (gameOver) return;

        if (InputKeys.isPressed("LEFT")) paddle.moveLeft();
        if (InputKeys.isPressed("RIGHT")) paddle.moveRight();

        List<Ball> allBalls = new ArrayList<>();
        allBalls.add(ball);
        allBalls.addAll(balls);

        List<PowerUp> newPowerUps = new ArrayList<>();
        for (Ball b : allBalls) {
            b.update();
            b.checkCollisionWithWalls(Config.WINDOW_WIDTH - 220, Config.WINDOW_HEIGHT);
            CollisionHandler.handleBallPaddleCollision(b, paddle);
            Iterator<Brick> iterator = level.getBricks().iterator();
            while (iterator.hasNext()) {
                Brick brick = iterator.next();
                if (CollisionHandler.handleBallBrickCollision(b, brick, newPowerUps, root)) {
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

        if (level instanceof Level5 lvl5) {
            var boss = lvl5.getBoss();
            if (boss != null) {
                boss.update();
                long nowTime = System.currentTimeMillis();
                if ((nowTime - lastFireballTime > 3000) || boss.getHealth() <= boss.getMaxHealth() / 2) {
                    lastFireballTime = nowTime;
                    double bx = boss.getView().getLayoutX() + boss.getView().getFitWidth() / 2;
                    double by = boss.getView().getLayoutY() + boss.getView().getFitHeight();
                    BossFireBall left = new BossFireBall(bx, by, 60, root);
                    BossFireBall mid = new BossFireBall(bx, by, 90, root);
                    BossFireBall right = new BossFireBall(bx, by, 120, root);
                    left.setOnHitListener(() -> handlePlayerHit());
                    mid.setOnHitListener(() -> handlePlayerHit());
                    right.setOnHitListener(() -> handlePlayerHit());
                    bossFireBalls.add(left);
                    bossFireBalls.add(mid);
                    bossFireBalls.add(right);
                }


                for (Ball b : allBalls) {
                    if (b.getView().getBoundsInParent().intersects(boss.getView().getBoundsInParent())) {
                        if (!boss.isDestroyed()) {
                            boss.takeDamage();
                            b.bounceOff(boss);
                            playerScore += 5;
                        }
                    }

                }

                var bullets = boss.getBullets();
                Iterator<BossBullet> bulletIter = bullets.iterator();
                while (bulletIter.hasNext()) {
                    var bullet = bulletIter.next();
                    bullet.update();
                    if (bullet.collidesWith(paddle)) {
                        root.getChildren().remove(bullet.getView());
                        bulletIter.remove();

                        if (!gameOver && playerLives > 0) {
                            playerLives--;
                            if (playerLives <= 0) {
                                handleGameOver();
                                return;
                            }
                        }
                    }

                }
                Iterator<BossFireBall> fireBallIterator = bossFireBalls.iterator();
                while (fireBallIterator.hasNext()) {
                    BossFireBall fb = fireBallIterator.next();
                    fb.update();
                    if (fb.collidesWith(paddle)) {
                        fireBallIterator.remove();
                    }
                }


                if (boss.isDestroyed()) {
                    nextLevel();
                    return;
                }
            }
        }

        if (level.getBricks().isEmpty()) nextLevel();

        if (ball.getY() > Config.WINDOW_HEIGHT && balls.isEmpty()) {
            if (!gameOver && playerLives > 0) { // ✅ thêm điều kiện kiểm tra
                playerLives--;
                if (playerLives <= 0) {
                    handleGameOver();
                    return;
                } else {
                    restartLevel();
                }
            }
        }


        hudPanel.updateHUD(currentLevel, playerScore, playerLives);
    }

    private void handleGameOver() {
        playerLives = 0;
        if (gameOver) return;
        gameOver = true;

        if (writer != null) {
            try {
                writer.write("PLAYER_DEAD\n");
                writer.flush();
            } catch (Exception ignored) {}
        }


        System.out.println("💀 GAME OVER!");
        org.example.baitaplamgame.Utlis.ScoreFileManager.saveScore(
                Config.PLAYER_NAME,
                playerScore,
                currentLevel
        );
        if (timer != null) timer.stop();
        restartLevel();
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
        timer.stop();
        currentLevel++;

        var nextLevelImageSource = new javafx.scene.image.Image(getClass().getResourceAsStream("/images/next.png"));
        var nextLevelImage = new ImageView(nextLevelImageSource);

        nextLevelImage.setFitWidth(500);
        nextLevelImage.setFitHeight(200);
        nextLevelImage.setLayoutX((Config.WINDOW_WIDTH - 220 - nextLevelImage.getFitWidth()) / 2);
        nextLevelImage.setLayoutY((Config.WINDOW_HEIGHT - nextLevelImage.getFitHeight()) / 2);
        nextLevelImage.setOpacity(0);
        root.getChildren().add(nextLevelImage);

        var fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.8), nextLevelImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        var pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
        var fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.2), nextLevelImage);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        var seq = new javafx.animation.SequentialTransition(fadeIn, pause, fadeOut);
        seq.setOnFinished(e -> {
            root.getChildren().remove(nextLevelImage);
            switch (currentLevel) {
                case 2 -> startLevel2();
                case 3, 4, 5 -> startLevel5();
                default -> {
                    System.out.println("🎉 Hoàn thành toàn bộ trò chơi!");
                    if (writer != null) {
                        try {
                            writer.write("PLAYER_SCORE_WIN\n");
                            writer.flush();
                        } catch (Exception ignored) {}
                    }
                }

            }
        });

        seq.play();
    }

    private void restartLevel() {
        System.out.println("🔄 Reset lại level " + currentLevel);
        gameOver = false;
        if (currentLevel == 1)
            startLevel1();
        else if (currentLevel == 2)
            startLevel2();
        else
            startLevel5();
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
    public void showGameOver(String msg) {
        System.out.println("=== GAME OVER EFFECT ===");

        javafx.scene.control.Label l = new javafx.scene.control.Label(msg);
        l.setStyle("-fx-font-size: 48px; -fx-text-fill: red; -fx-font-weight: bold;");
        l.setLayoutX((Config.WINDOW_WIDTH - 220)/2 - 150);
        l.setLayoutY(Config.WINDOW_HEIGHT/2 - 100);

        root.getChildren().add(l);
    }

    public void showWinnerEffect() {
        System.out.println("=== WINNER EFFECT ===");

        javafx.scene.control.Label l = new javafx.scene.control.Label("YOU WIN!");
        l.setStyle("-fx-font-size: 48px; -fx-text-fill: yellow; -fx-font-weight: bold;");
        l.setLayoutX((Config.WINDOW_WIDTH - 220)/2 - 150);
        l.setLayoutY(Config.WINDOW_HEIGHT/2 - 100);

        root.getChildren().add(l);
    }

    public void handlePlayerHit() {
        if (gameOver) return;

        playerLives--;
        System.out.println("🔥 Paddle bị Boss tấn công! Máu còn lại: " + playerLives);

        // Hiển thị hiệu ứng trúng đạn
        javafx.scene.shape.Circle hitEffect = new javafx.scene.shape.Circle(
                paddle.getX() + paddle.getWidth() / 2,
                paddle.getY() + paddle.getHeight() / 2,
                40,
                javafx.scene.paint.Color.RED
        );
        hitEffect.setOpacity(0.5);
        root.getChildren().add(hitEffect);

        var fade = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.5), hitEffect);
        fade.setFromValue(0.7);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> root.getChildren().remove(hitEffect));
        fade.play();

        hudPanel.updateHUD(currentLevel, playerScore, playerLives);

        if (playerLives <= 0) {
            handleGameOver();
        }
    }


}
