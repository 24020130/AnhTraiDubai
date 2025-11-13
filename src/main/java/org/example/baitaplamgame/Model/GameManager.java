package org.example.baitaplamgame.Model;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import org.example.baitaplamgame.Level.*;
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
    private boolean levelCleared = false;
    private Runnable onExitToMenu; // đã có
    private java.util.function.IntConsumer onLevelComplete; // thêm dòng này
    private boolean gameOver = false;
    private org.example.baitaplamgame.Ui.HUDPanel hudPanel;
    private java.io.BufferedWriter writer;
    private Runnable onGameOver;
    public void setOnGameOver(Runnable onGameOver) { this.onGameOver = onGameOver; }
    public void setWriter(java.io.BufferedWriter w) { this.writer = w; }
    public void setOnExitToMenu(Runnable callback) {
        this.onExitToMenu = callback;
    }
    public void setOnLevelComplete(java.util.function.IntConsumer onLevelComplete) {
        this.onLevelComplete = onLevelComplete;
    }


    public GameManager(Pane root, double width, double height) {
        this.root = root;
    }
    public void startGame() {
        currentLevel = 1;
        playerLives = 6;
        playerScore = 0;
        gameOver = false;

        switch (currentLevel) {
            case 1 -> startLevel1();
            case 2 -> startLevel2();
            case 3 -> startLevel3();
            case 4 -> startLevel4();
            case 5 -> startLevel5();
            case 6 -> startLevel6();
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

    private void startLevel5() {
        startLevel(new Level5(5), "/levels/level5.txt");
    }
    private void startLevel6() {
        startLevel(new Level6(6), "/levels/level6.txt");
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

        bg2.setLayoutX(bg1.getFitWidth());

        root.getChildren().addAll(bg1, bg2);

        paddle = new Paddle(
                350,
                625,
                100,
                20,
                Config.PADDLE_SPEED,
                this,
                Config.CURRENT_PLAYER_SKIN
        );
        ball = new Ball(390, 500, 20, Config.BALL_SPEED);
        paddle.setBall(ball);
        this.level = levelObj;
        level.generateLevelFromFile(filePath, root);

        root.getChildren().addAll(paddle.getView(), ball.getView());
        if (currentLevel == 4) {
            supportPaddle = new SupportPaddle(paddle.getX(),paddle.getY()+20, 80, 20, "lv4");
            root.getChildren().add(supportPaddle.getView());

        }
        if (currentLevel == 5) {
            supportPaddle = new SupportPaddle(paddle.getX(),paddle.getY()+20, 80, 20, "lv5");
            root.getChildren().add(supportPaddle.getView());

        }
        if (currentLevel == 6) {
            supportPaddle = new SupportPaddle(paddle.getX(),paddle.getY()+20, 80, 20, "lv6");
            root.getChildren().add(supportPaddle.getView());

        }
        hudPanel = new org.example.baitaplamgame.Ui.HUDPanel(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        hudPanel.updateHUD(currentLevel, playerScore, playerLives);
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
        paddle.update();
        List<Ball> allBalls = new ArrayList<>();
        allBalls.add(ball);
        allBalls.addAll(balls);
        if (supportPaddle != null) {
            supportPaddle.update();
            Iterator<Laze> lazeIterator = supportPaddle.getLazeList().iterator();
            Boss boss = null;
            if (level instanceof Level6 lvl6) {
                boss = lvl6.getBoss();
            }

            while (lazeIterator.hasNext()) {
                Laze l = lazeIterator.next();
                if (!root.getChildren().contains(l.getView())) {
                    root.getChildren().add(l.getView());
                }

                boolean lazeRemoved = false;
                if (boss != null && !boss.isDestroyed() && CollisionHandler.checkCollision(l, boss)) {
                    boss.takeDamage(); // Giả định Boss có phương thức takeDamage()
                    playerScore += 20; // Thưởng điểm khi bắn trúng Boss

                    root.getChildren().remove(l.getView());
                    lazeIterator.remove();
                    lazeRemoved = true;
                }
                if (!lazeRemoved) {
                    Iterator<Brick> brickIterator = level.getBricks().iterator();
                    while (brickIterator.hasNext()) {
                        Brick brick = brickIterator.next();
                        if (!brick.isDestroyed() && CollisionHandler.checkCollision(l, brick)) {
                            playerScore += 5;
                            brick.takeHit();

                            root.getChildren().remove(l.getView());
                            lazeIterator.remove(); // ✅ Xóa an toàn
                            lazeRemoved = true;
                            break;
                        }
                    }
                }
            }
        }

        // Ball + Brick collision
        List<PowerUp> newPowerUps = new ArrayList<>();
        List<Brick> bricksToRemove = new ArrayList<>();

        for (Ball b : allBalls) {
            b.update();
            b.checkCollisionWithWalls(Config.WINDOW_WIDTH - 220, Config.WINDOW_HEIGHT);

            if (!b.hasCollidedWithPaddle() && CollisionHandler.checkCollision(b, paddle)) {
                CollisionHandler.handleBallPaddleCollision(b, paddle);
                SoundManager.playEffect("collision2.mp3");
                b.setHasCollidedWithPaddle(true);
            } else if (!CollisionHandler.checkCollision(b, paddle)) {
                b.setHasCollidedWithPaddle(false);
            }

            // Duyệt bricks bằng copy để tránh ConcurrentModification
            for (Brick brick : new ArrayList<>(level.getBricks())) {
                if (CollisionHandler.handleBallBrickCollision(b, brick, newPowerUps, root)) {
                    SoundManager.playEffect("collision.mp3");
                    if (brick.isDestroyed()) {
                        bricksToRemove.add(brick); // ✅ Xóa sau vòng lặp
                        playerScore += 10;
                    }
                    break;
                }
            }
        }

        // Xóa tất cả brick đã phá hủy
        level.getBricks().removeAll(bricksToRemove);

        // PowerUp collision
        for (PowerUp p : newPowerUps) addPowerUp(p);
        CollisionHandler.handlePowerUpCollision(powerUps, paddle, root);

        // Remove balls out of screen
        balls.removeIf(b -> {
            if (b.getY() > Config.WINDOW_HEIGHT) {
                root.getChildren().remove(b.getView());
                return true;
            }
            return false;
        });

        // Level Boss
        if (level instanceof Level6 lvl6) {
            var boss = lvl6.getBoss();
            if (boss != null) {
                boss.update();

                // Hiển thị Boss HP
                if (!boss.isDestroyed()) {
                    hudPanel.showBossHP(true);
                    hudPanel.updateBossHP(boss.getHealth(), boss.getMaxHealth());
                } else {
                    hudPanel.showBossHP(false);
                }


                // Spawn BossFireBall

                long nowTime = System.currentTimeMillis();
                long cooldown = boss.getHealth() <= boss.getMaxHealth() / 2 ? 5000 : 7000;
                int maxTotalFireballs = 30;
                if (nowTime - lastFireballTime > cooldown && bossFireBalls.size() < maxTotalFireballs) {
                    lastFireballTime = nowTime;
                    double bx = boss.getView().getLayoutX() + boss.getView().getFitWidth() / 2;
                    double by = boss.getView().getLayoutY() + boss.getView().getFitHeight();

                    BossFireBall left = new BossFireBall(bx, by, 60, root);
                    BossFireBall mid = new BossFireBall(bx, by, 90, root);
                    BossFireBall right = new BossFireBall(bx, by, 120, root);

                    left.setOnHitListener(this::handlePlayerHit);
                    mid.setOnHitListener(this::handlePlayerHit);
                    right.setOnHitListener(this::handlePlayerHit);

                    bossFireBalls.add(left);
                    bossFireBalls.add(mid);
                    bossFireBalls.add(right);
                }
                // Ball va chạm Boss
                for (Ball b : allBalls) {
                    if (b.getView().getBoundsInParent().intersects(boss.getView().getBoundsInParent())) {
                        if (!boss.isDestroyed()) {
                            boss.takeDamage();
                            b.bounceOff(boss);
                            playerScore += 5;
                        }
                    }
                }

                // Boss bullets
                Iterator<BossBullet> it = boss.getBullets().iterator();
                while (it.hasNext()) {
                    BossBullet b = it.next();
                    b.update();

                    if (b.collidesWith(paddle) || b.getView().getLayoutY() > Config.WINDOW_HEIGHT) {
                        root.getChildren().remove(b.getView());
                        it.remove(); // xóa an toàn khi đang lặp
                    }
                }


                // Boss fireballs
                List<BossFireBall> toRemove = new ArrayList<>();

                for (BossFireBall fb : new ArrayList<>(bossFireBalls)) {
                    fb.update();

                    if (fb.collidesWith(paddle)) {
                        root.getChildren().remove(fb.getView());
                        toRemove.add(fb);
                    }
                }

                bossFireBalls.removeAll(toRemove);

            }
        } else {
            // Nếu không phải Level6 → ẩn luôn Boss HP
            hudPanel.showBossHP(false);
        }

        // Check level cleared
        if (level.getBricks().isEmpty()) nextLevel();

        // Ball rơi hết
        if (ball.getY() > Config.WINDOW_HEIGHT && balls.isEmpty()) {
            handleBallLost();
        }

        // Update HUD
        hudPanel.updateHUD(currentLevel, playerScore, playerLives);

        // Background scrolling
        bg1.setLayoutX(bg1.getLayoutX() - bgSpeed);
        bg2.setLayoutX(bg2.getLayoutX() - bgSpeed);
        if (bg1.getLayoutX() + bg1.getFitWidth() <= 0) bg1.setLayoutX(bg2.getLayoutX() + bg2.getFitWidth());
        if (bg2.getLayoutX() + bg2.getFitWidth() <= 0) bg2.setLayoutX(bg1.getLayoutX() + bg1.getFitWidth());

        // Meteor spawn
        long currentTime = System.nanoTime();
        if (!gameOver && currentTime - lastMeteorSpawn > 5_000_000_000L) {
            double x = Math.random() * (Config.WINDOW_WIDTH - 260);
            Meteor m = new Meteor(x, -50);
            meteors.add(m);
            root.getChildren().add(m);
            lastMeteorSpawn = currentTime;
        }

        // Update meteors
        List<Meteor> meteorsToRemove = new ArrayList<>();
        for (Meteor m : new ArrayList<>(meteors)) {
            m.update(root);

            if (m.getBoundsInParent().intersects(paddle.getView().getBoundsInParent())) {
                playerLives--;
                if(playerLives <= 0) {
                    handleGameOver();
                }
                if (m.isActive()) m.destroy(root);
                meteorsToRemove.add(m);
            } else if (!m.isActive() || m.getLayoutY() > Config.WINDOW_HEIGHT) {
                if (m.isActive()) m.destroy(root);
                meteorsToRemove.add(m);
            }
        }

        meteors.removeAll(meteorsToRemove);
    }



    private void handleGameOver() {
        if (gameOver) return; // tránh gọi lại nhiều lần
        gameOver = true;

        showGameOver("GAME OVER");

        if (timer != null) timer.stop();

        meteors.forEach(m -> root.getChildren().remove(m));
        meteors.clear();
        bossFireBalls.forEach(fb -> root.getChildren().remove(fb.getView()));
        bossFireBalls.clear();
        balls.forEach(b -> root.getChildren().remove(b.getView()));
        balls.clear();
        if (supportPaddle != null) {
            supportPaddle.getLazeList().forEach(l -> root.getChildren().remove(l.getView()));
            supportPaddle.getLazeList().clear();
        }

        InputKeys.clearAll();

        if (writer != null) {
            try {
                writer.write("PLAYER_DEAD\n");
                writer.flush();
            } catch (Exception ignored) {}
        }

        // ⏳ 3s sau mới quay lại menu
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> {
            if (onGameOver != null) onGameOver.run();
        });
        pause.play();
    }




    public void spawnExtraBalls(Ball sourceBall, int count) {
        if (sourceBall == null || count <= 0) return;

        double baseX = sourceBall.getX();
        double baseY = sourceBall.getY();
        double baseSpeed = sourceBall.getSpeed();
        double baseWidth = sourceBall.getView().getFitWidth();

        for (int i = 0; i < count; i++) {
            Ball newBall = new Ball(baseX, baseY, baseWidth, baseSpeed);

            // Random hướng bay
            double vx = sourceBall.getVelocityX() + (Math.random() - 0.5) * 4;
            double vy = -Math.abs(sourceBall.getVelocityY());

            newBall.setVelocity(vx, vy);
            balls.add(newBall);
            root.getChildren().add(newBall.getView());
        }
    }


    private void nextLevel() {
        if (levelCleared) return;
        levelCleared = true;

        // lưu giá trị level vừa hoàn thành
        final int completedLevel = currentLevel;
        if (timer != null) {
            timer.stop();
        }

        // tăng currentLevel để sẵn sàng cho lần start sau (nhưng *không* tự start)
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
            // dọn hình next
            root.getChildren().remove(nextLevelImage);
            levelCleared = false;

            // *Báo về level đã hoàn thành* (gửi số của level vừa hoàn thành)
            handleLevelComplete(completedLevel);

            // **KHÔNG tự chạy next level ở đây nữa.**
            // Việc start level mới sẽ do GamePanel (user) chọn khi họ bấm.
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
        else if(currentLevel == 3) {
            startLevel3();
        }
        else if(currentLevel == 4) {
            startLevel4();
        } else if (currentLevel == 5) {
            startLevel5();
        }
        else {
            startLevel6();
        }
    }

    public void setupInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT -> paddle.moveLeft();
                case RIGHT -> paddle.moveRight();
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case LEFT, RIGHT -> paddle.stop();
            }
        });
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
            if (onGameOver != null) onGameOver.run();
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
    public void handleBallLost() {
        if (gameOver) return;

        playerLives--;
        System.out.println("💥 Mất bóng! Mạng còn lại: " + playerLives);
        hudPanel.updateHUD(currentLevel, playerScore, playerLives);

        if (playerLives > 0) {
            resetBallPosition(); // chỉ reset bóng
        } else {
            handleGameOver(); // hết mạng thì game over
        }
    }

    // --- Thêm vào cuối GameManager ---
    /**
     * Bắt đầu trực tiếp một level cụ thể (1..6).
     * Sử dụng khi gọi từ menu chọn level.
     */
    public void startLevelNumber(int level) {
        // dừng timer nếu đang chạy
        if (timer != null) timer.stop();

        // đặt level hiện tại rồi gọi start tương ứng
        this.currentLevel = Math.max(1, Math.min(level, 6));
        levelCleared = false;
        gameOver = false;
        this.playerLives = 6;
        switch (this.currentLevel) {
            case 1 -> startLevel1();
            case 2 -> startLevel2();
            case 3 -> startLevel3();
            case 4 -> startLevel4();
            case 5 -> startLevel5();
            case 6 -> startLevel6();
            default -> startLevel1();
        }
    }
    // Gọi khi người chơi hoàn thành một level
    // ✅ Gọi khi người chơi hoàn thành một level
    // trước: private void handleLevelComplete() { ... }
// sửa thành:
    private void handleLevelComplete(int completedLevel) {
        System.out.println("✅ Level " + completedLevel + " hoàn thành!");

        // gọi callback nếu có
        if (onLevelComplete != null) {
            onLevelComplete.accept(completedLevel);
        }
    }
    public void stopGame() {
        if (timer != null) timer.stop();
    }
    private void resetBallPosition() {
        ball.resetPosition(paddle.getX() + paddle.getWidth() / 2, paddle.getY() - 30);
        ball.setVelocity(0, -Math.abs(ball.getSpeed())); // bay lên trên

        // Nếu node bóng chưa có trong root → add lại
        if (!root.getChildren().contains(ball.getView())) {
            root.getChildren().add(ball.getView());
        }

        // Đưa bóng ra trước cùng để không bị đè bởi đuôi
        ball.getView().toFront();

        // Bật hiển thị và reset hiệu ứng
        ball.getView().setOpacity(1);

        // Hiệu ứng hiện dần
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(400), ball.getView());
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }



}