package org.example.baitaplamgame.Model;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
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
    private final List<BossFireBall> bossFireBalls = new ArrayList<>();
    private long lastFireballTime = 0;
    private AnimationTimer timer;
    private SupportPaddle supportPaddle;
    private int currentLevel = 1;
    private int playerScore = 0;
    private int playerLives = 4;
    private Image bgImage;
    private ImageView bg1, bg2;
    private double bgSpeed = 0.2;
    private final List<Meteor> meteors = new ArrayList<>();
    private long lastMeteorSpawn = 0;

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

        if (supportPaddle != null) {
            root.getChildren().remove(supportPaddle.getView());
            supportPaddle = null;
        }
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
        if (currentLevel == 2) {
            supportPaddle = new SupportPaddle(paddle.getX(),paddle.getY()+20, 80, 20);
            root.getChildren().add(supportPaddle.getView());

        }
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
            private long lastUpdate = 0;
            private static final long FRAME_INTERVAL = 10_000_000; // ~100 FPS

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= FRAME_INTERVAL) {
                    update();
                    lastUpdate = now;
                }
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

        if (supportPaddle != null) {
            supportPaddle.update();
            // cập nhật Laze
            for (Laze l : supportPaddle.getLazeList()) {
                if (!root.getChildren().contains(l.getView())) {
                    root.getChildren().add(l.getView());
                }
            }

            // xử lý va chạm và xóa Laze khi ra khỏi màn hình
            Iterator<Laze> lazeIterator = supportPaddle.getLazeList().iterator();
            while (lazeIterator.hasNext()) {
                Laze l = lazeIterator.next();
                Iterator<Brick> brickIterator = level.getBricks().iterator();
                while (brickIterator.hasNext()) {
                    Brick brick = brickIterator.next();
                    if (!brick.isDestroyed() && CollisionHandler.checkCollision(l, brick)) {
                        playerScore += 5;
                        brick.takeHit();

                        // Xóa brick ra khỏi danh sách và khỏi màn hình
                        // Hiệu ứng nhấp nháy trước khi biến mất
                        javafx.animation.Timeline flash = new javafx.animation.Timeline(
                                new javafx.animation.KeyFrame(javafx.util.Duration.millis(50),
                                        e -> brick.getView().setOpacity(0.3)),
                                new javafx.animation.KeyFrame(javafx.util.Duration.millis(100),
                                        e -> brick.getView().setOpacity(1.0))
                        );
                        flash.setCycleCount(4); // nhấp nháy 4 lần
                        flash.setAutoReverse(true);

                        flash.setOnFinished(e -> {
                            root.getChildren().remove(brick.getView());
                        });


                        brickIterator.remove();
                        flash.play();

                        // Xóa Laze
                        lazeIterator.remove();
                        root.getChildren().remove(l.getView());
                        break;
                    }
                }
            }

        }
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

    private void handleGameOver() {
        playerLives = 0;
        if (gameOver) return;
        gameOver = true;

        // Ghi trạng thái vào writer nếu có
        if (writer != null) {
            try {
                writer.write("PLAYER_DEAD\n");
                writer.flush();
            } catch (Exception ignored) {}
        }

        System.out.println("💀 GAME OVER!");

        // Lưu điểm số
        org.example.baitaplamgame.Utlis.ScoreFileManager.saveScore(
                Config.PLAYER_NAME,
                playerScore,
                currentLevel
        );

        // Dừng timer game
        if (timer != null) timer.stop();

        // Hiển thị hiệu ứng Game Over
        showGameOver("GAME OVER");

        // Trì hoãn reset level để người chơi nhìn thấy hiệu ứng
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.5));
        pause.setOnFinished(e -> restartLevel());
        pause.play();
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
        javafx.scene.text.Text text = new javafx.scene.text.Text(msg);
        text.setStyle("-fx-font-size: 80px; -fx-font-weight: bold;");
        text.setFill(javafx.scene.paint.Color.RED);
        text.setStroke(javafx.scene.paint.Color.BLACK);
        text.setStrokeWidth(3);

        text.setX((Config.WINDOW_WIDTH - 220) / 2 - 200);
        text.setY(Config.WINDOW_HEIGHT / 2);

        root.getChildren().add(text);

        // Gradient chuyển động
        javafx.scene.paint.LinearGradient gradient = new javafx.scene.paint.LinearGradient(
                0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.REPEAT,
                new javafx.scene.paint.Stop[]{
                        new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.RED),
                        new javafx.scene.paint.Stop(0.5, javafx.scene.paint.Color.ORANGE),
                        new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.YELLOW)
                });
        text.setFill(gradient);

        // Scale + Fade
        javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(javafx.util.Duration.seconds(1.5), text);
        scale.setFromX(0.5); scale.setFromY(0.5);
        scale.setToX(1.5); scale.setToY(1.5);

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.5), text);
        fade.setFromValue(0); fade.setToValue(1);

        javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(scale, fade);
        pt.play();

        // Particle explosion nhiều màu
        for (int i = 0; i < 60; i++) {
            javafx.scene.shape.Circle p = new javafx.scene.shape.Circle(4 + Math.random() * 6,
                    javafx.scene.paint.Color.color(Math.random(), Math.random(), Math.random()));
            p.setLayoutX(text.getX() + 200);
            p.setLayoutY(text.getY() - 50);
            root.getChildren().add(p);

            double angle = Math.random() * 2 * Math.PI;
            double distance = 100 + Math.random() * 100;

            javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(1 + Math.random()), p);
            tt.setByX(Math.cos(angle) * distance);
            tt.setByY(Math.sin(angle) * distance + Math.random() * 50);
            tt.setOnFinished(e -> root.getChildren().remove(p));
            tt.play();
        }

        // Shake effect toàn màn hình
        javafx.animation.TranslateTransition shake = new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(0.05), root);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();

        // Trì hoãn và quay về màn hình chính
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> {
            if (onExitToMenu != null) {
                onExitToMenu.run();  // quay về menu chính
            }
        });
        pause.play();
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
