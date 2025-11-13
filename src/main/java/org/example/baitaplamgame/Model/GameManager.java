package org.example.baitaplamgame.Model;

import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow; // ✅


import javafx.util.Duration;
import org.example.baitaplamgame.Level.*;
import org.example.baitaplamgame.PowerUp.BossFireBall;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.InputKeys;
import org.example.baitaplamgame.PowerUp.BossBullet;
import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Utlis.SoundManager;

// LOẠI BỎ hoặc thay thế nếu cần: java.awt.*
// import java.awt.*;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class GameManager {
    private final Pane root;
    private Paddle paddle;
    private Ball ball;
    private List<Ball> balls = new ArrayList<>();
    private java.util.function.Consumer<Boolean> onGameEnd;
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
    private Runnable onExitToMenu;
    private java.util.function.IntConsumer onLevelComplete;
    private boolean gameOver = false;
    private org.example.baitaplamgame.Ui.HUDPanel hudPanel;
    private java.io.BufferedWriter writer;
    private Runnable onGameOver;
    private Consumer<Double> onPaddleMove;
    private Runnable onGameEndToMenu;

    public void setOnGameEndToMenu(Runnable callback) {
        this.onGameEndToMenu = callback;
    }

    private Paddle enemyPaddle;
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
        SoundManager.playEffect("boss_sound.mp3");
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

        if (levelObj instanceof Level6) {
            hudPanel.showBossHP(true);
            Boss boss = ((Level6) levelObj).getBoss();
            if (boss != null) {
                hudPanel.updateBossHP(boss.getHealth(), boss.getMaxHealth());
            }
        } else {
            hudPanel.showBossHP(false);
        }


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
                    boss.takeDamage();
                    playerScore += 20;
                    SoundManager.playEffect("laze_sound.mp3");
                    if (boss.getHealth() >= 0) {
                        hudPanel.updateBossHP(boss.getHealth(), boss.getMaxHealth());
                    }
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

                // Spawn BossFireBall

                long nowTime = System.currentTimeMillis();
                long cooldown = boss.getHealth() <= boss.getMaxHealth() / 2 ? 5000 : 7000;
                int maxTotalFireballs = 30;
                if (nowTime - lastFireballTime > cooldown && bossFireBalls.size() < maxTotalFireballs) {
                    lastFireballTime = nowTime;
                    SoundManager.playEffect("boss_fire.mp3");
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
                            hudPanel.updateBossHP(boss.getHealth(), boss.getMaxHealth());
                            b.bounceOff(boss);
                            playerScore += 5;
                        }
                    }
                }
                if (boss.isDestroyed()) {
                    if (onGameEnd != null && !gameOver) {
                        endGame(true); // Player WIN
                        return;
                    } else if (onGameEnd == null && !gameOver) {
                        showWinScreen();
                        return;
                    }
                }

                // Boss bullets
                Iterator<BossBullet> it = boss.getBullets().iterator();
                while (it.hasNext()) {
                    BossBullet b = it.next();
                    b.update();

                    if (b.collidesWith(paddle) || b.getView().getLayoutY() > Config.WINDOW_HEIGHT) {
                        root.getChildren().remove(b.getView());
                        handlePlayerHit();
                        it.remove();
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


        // Xử lý va chạm meteor qua CollisionHandler
        int lostLives = CollisionHandler.handleMeteorPaddleCollision(meteors, paddle, root);
        playerLives -= lostLives;
        if (playerLives <= 0) {
            handleGameOver();
        }
    }



    private void handleGameOver() {
        if (gameOver) return;
        gameOver = true;


        // Hiển thị hiệu ứng GAME OVER
        showGameOver("GAME OVER");
        SoundManager.stopBackground();
        SoundManager.stopAllEffects();

        if (timer != null) timer.stop();

        // Dọn dẹp các đối tượng game (Giữ logic cũ)
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

        // Logic ghi log (Giữ logic cũ)
        if (writer != null) {
            try {
                writer.write("PLAYER_DEAD\n");
                writer.flush();
            } catch (Exception ignored) {}
        }

        // Tạm dừng 3s rồi quay lại menu
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> {
            if (onGameEndToMenu != null) onGameEndToMenu.run(); // ✅ Dùng callback chung
        });
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
            System.out.println("Key pressed: " + e.getCode());
            switch (e.getCode()) {
                case LEFT -> paddle.moveLeft();
                case RIGHT -> paddle.moveRight();
            }
        });

        scene.setOnKeyReleased(e -> {
            System.out.println("Key released: " + e.getCode());
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
        SoundManager.stopBackground();
        SoundManager.stopAllEffects();
        SoundManager.playEffect("game_over.mp3");
        StackPane overlay = new StackPane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        // Nền tối hơn nữa
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.98);");
        root.getChildren().add(overlay);

        // BƯỚC 2: Tạo chữ GAME OVER (Neon Glitch Effect)
        javafx.scene.text.Text text = new javafx.scene.text.Text("--- SYSTEM FAILURE ---"); // Thay thế GAME OVER bằng thông điệp kịch tính
        text.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 85));

        Color neonRed = Color.web("#FF4444");
        Color neonPink = Color.web("#FF0066");

        // Hiệu ứng phát sáng mạnh
        DropShadow glow = new DropShadow(50, neonPink);
        glow.setInput(new DropShadow(25, neonRed));
        text.setEffect(glow);
        text.setFill(neonRed);
        text.setOpacity(0);

        overlay.getChildren().add(text); // Thêm vào overlay

        // BƯỚC 3: Hiệu ứng Xuất hiện (FadeIn) và Rung lắc (Heavy Shake)
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), text);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // Hiệu ứng Rung lắc mạnh
        TranslateTransition textShake = new TranslateTransition(Duration.millis(50), text);
        textShake.setByX(8);
        textShake.setCycleCount(10);
        textShake.setAutoReverse(true);
        textShake.play();

        // Hiệu ứng Glitch liên tục (Nhiễu màn hình)
        Timeline glitchTimeline = new Timeline(
                new KeyFrame(Duration.millis(50), new KeyValue(text.translateXProperty(), Math.random() * 5 - 2.5)),
                new KeyFrame(Duration.millis(100), new KeyValue(text.translateXProperty(), 0))
        );
        glitchTimeline.setCycleCount(Animation.INDEFINITE);
        glitchTimeline.play();

        // BƯỚC 4: Hiệu ứng Hạt Tan Rã (Destruction Particles)
        for (int i = 0; i < 80; i++) { // Tăng số lượng hạt
            javafx.scene.shape.Circle p = new javafx.scene.shape.Circle(
                    2 + Math.random() * 4,
                    (Math.random() < 0.7) ? neonRed : Color.WHITE // Hạt chủ yếu là Đỏ Neon và Trắng
            );

            // Spawn hạt ngay tại trung tâm chữ
            p.setTranslateX(Math.random() * 100 - 50);
            p.setTranslateY(Math.random() * 50 - 25);
            overlay.getChildren().add(p);
            DropShadow shadow = new DropShadow(45.0, (Color) Color.web("#FF1177"));

            double angle = Math.random() * 2 * Math.PI;
            double distance = 150 + Math.random() * 150; // Bay xa hơn

            TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5 + Math.random()), p);
            tt.setByX(Math.cos(angle) * distance);
            tt.setByY(Math.sin(angle) * distance + Math.random() * 80); // Rơi mạnh

            FadeTransition ft = new FadeTransition(Duration.seconds(2.0), p);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);

            ParallelTransition pt = new ParallelTransition(p, tt, ft);
            pt.setOnFinished(e -> overlay.getChildren().remove(p));
            pt.play();
        }

        // BƯỚC 5: Hiệu ứng Scanline (Lớp phủ lỗi kỹ thuật số)
        Rectangle scanline = new Rectangle(Config.WINDOW_WIDTH, 5, Color.web("#FFFFFF20"));
        overlay.getChildren().add(scanline);
        scanline.setTranslateY(-Config.WINDOW_HEIGHT / 2); // Đặt ở trên cùng StackPane

        TranslateTransition scan = new TranslateTransition(Duration.seconds(0.05), scanline);
        scan.setByY(Config.WINDOW_HEIGHT + 10);
        scan.setCycleCount(Animation.INDEFINITE);
        scan.setAutoReverse(true); // Quét lên xuống
        scan.play();

        // BƯỚC 6: Trì hoãn và quay về màn hình chính
        PauseTransition finalPause = new PauseTransition(Duration.seconds(4.0)); // Tăng thời gian dừng
        finalPause.setOnFinished(e -> {
            if (onGameOver != null) onGameOver.run();
        });
        finalPause.play();
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
            // ✨ SỬA CHỮA: Phân biệt chế độ Multiplayer
            if (onGameEnd != null) {
                endGame(false); // Multiplayer: Player hiện tại THUA
            } else {
                handleGameOver(); // Single-player
            }
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
            // ✨ SỬA CHỮA: Đây là lỗi chính trong code cũ của bạn
            if (onGameEnd != null) {
                // MULTIPLAYER: Kích hoạt callback mạng (Người chơi hiện tại THUA)
                endGame(false);
            } else {
                // SINGLE-PLAYER: Kích hoạt logic Game Over cổ điển
                handleGameOver();
            }
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


    public void movePlayerUp() {
        paddle.setY(Math.max(0, paddle.getY() - Config.PADDLE_SPEED));
        sendPaddlePosition();
    }

    public void movePlayerDown() {
        paddle.setY(Math.min(Config.WINDOW_HEIGHT - paddle.getHeight(), paddle.getY() + Config.PADDLE_SPEED));
        sendPaddlePosition();
    }

    // gửi vị trí cho client
    private void sendPaddlePosition() {
        if (onPaddleMove != null) {
            onPaddleMove.accept(paddle.getY());
        }
    }
    public void setPaddleY(double y) {
        paddle.setY(y);
    }
    public void setOnPaddleMove(Consumer<Double> callback) {
        this.onPaddleMove = callback;
    }


    // Hoặc nếu muốn trả về paddle để client dùng trực tiếp
    public Paddle getPaddle() {
        return paddle;
    }
    public void setOnGameEnd(java.util.function.Consumer<Boolean> onGameEnd) {
        this.onGameEnd = onGameEnd;
    }

    public void endGame(boolean isPlayerWin) {
        if (gameOver) return;
        gameOver = true;

        stopGame();

        // Chỉ gửi kết quả qua mạng (Server/Client sẽ tự hiển thị WIN/LOSE)
        if (onGameEnd != null) {
            onGameEnd.accept(isPlayerWin);
        }
    }
    public void showGameResult(String result) {
        if (gameOver) return; // Ngăn gọi nhiều lần
        gameOver = true;
        stopGame();

        // Dừng âm thanh
        SoundManager.stopBackground();
        SoundManager.stopAllEffects();

        // Dọn dẹp màn hình, giữ background và HUD
        root.getChildren().removeIf(node -> node != bg1 && node != bg2 && node != hudPanel);

        meteors.clear();
        bossFireBalls.clear();
        balls.clear();
        powerUps.clear();
        if (supportPaddle != null) supportPaddle.getLazeList().clear();
        InputKeys.clearAll();

        // --- Tạo chữ WIN / LOSE ---
        String message = result.equalsIgnoreCase("WIN") ? "🏆 YOU WIN! 🏆" : "💀 YOU LOSE 💀";

        Text text = new Text(message);
        text.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 80));
        Color neonColor = result.equalsIgnoreCase("WIN") ? Color.LIME : Color.RED;
        text.setFill(neonColor);

        // Glow mạnh
        DropShadow glow = new DropShadow(50, neonColor);
        glow.setInput(new DropShadow(25, Color.BLACK));
        text.setEffect(glow);

        // Căn giữa màn hình
        double textWidth = text.getLayoutBounds().getWidth();
        text.setX((Config.WINDOW_WIDTH - 220 - textWidth) / 2);
        text.setY(Config.WINDOW_HEIGHT / 2);
        text.setOpacity(0);

        root.getChildren().add(text);
        text.toFront();

        // Fade in chữ
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), text);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Neon nhấp nháy
        Timeline flicker = new Timeline(
                new KeyFrame(Duration.seconds(0.0), new KeyValue(text.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(text.opacityProperty(), 0.6)),
                new KeyFrame(Duration.seconds(0.15), new KeyValue(text.opacityProperty(), 1))
        );
        flicker.setCycleCount(Animation.INDEFINITE);
        flicker.setAutoReverse(true);
        flicker.play();

        // Particle bay neon
        StackPane overlay = new StackPane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        root.getChildren().add(overlay);
        overlay.toBack();

        Color[] neonColors = { Color.web("#FF1177"), Color.web("#FF44AA"), Color.web("#44FFFF"), Color.web("#FFDD00") };

        Timeline particleLoop = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            for (int i = 0; i < 20; i++) {
                Circle p = new Circle(1 + Math.random() * 2, neonColors[(int) (Math.random() * neonColors.length)]);
                p.setTranslateX(0);
                p.setTranslateY(0);
                overlay.getChildren().add(p);
                p.toBack();

                double angle = Math.random() * 2 * Math.PI;
                double distance = 200 + Math.random() * 200;

                TranslateTransition tt = new TranslateTransition(Duration.seconds(2 + Math.random()), p);
                tt.setByX(Math.cos(angle) * distance);
                tt.setByY(Math.sin(angle) * distance + Math.random() * 50);

                FadeTransition ft = new FadeTransition(Duration.seconds(2 + Math.random()), p);
                ft.setFromValue(1);
                ft.setToValue(0);

                ParallelTransition pt = new ParallelTransition(p, tt, ft);
                pt.setOnFinished(ev -> overlay.getChildren().remove(p));
                pt.play();
            }
        }));
        particleLoop.setCycleCount(Animation.INDEFINITE);
        particleLoop.play();

        // Quay về menu sau 6 giây
        PauseTransition delay = new PauseTransition(Duration.seconds(6));
        delay.setOnFinished(e -> {
            if (onGameEndToMenu != null) onGameEndToMenu.run();
        });
        delay.play();
    }

    private void showWinScreen() {
        if (gameOver) return;
        gameOver = true;

        stopGame();
        SoundManager.stopBackground();
        SoundManager.stopAllEffects();
        SoundManager.playEffect("win_sound.mp3"); // Âm thanh thắng hiện đại

        // Dọn root, giữ lại bg, hud
        root.getChildren().removeIf(node ->
                node != bg1 &&
                        node != bg2 &&
                        node != hudPanel
        );

        // Overlay tối, hiệu ứng nhiễu nhẹ
        StackPane overlay = new StackPane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.92);");
        root.getChildren().add(overlay);

        // ====== YOU WIN Neon Text ======
        Text winText = new Text(">>> VICTORY UNLOCKED <<<");
        winText.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 78));
        winText.setFill(Color.web("#FF0066"));
        winText.setEffect(new DropShadow(45, Color.web("#FF1177")));
        winText.setOpacity(0);
        overlay.getChildren().add(winText);

        // Hiệu ứng Fade + Glow mạnh dần
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), winText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Hiệu ứng nhấp nháy Neon
        Timeline flicker = new Timeline(
                new KeyFrame(Duration.seconds(0.0), new KeyValue(winText.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(0.1), new KeyValue(winText.opacityProperty(), 0.6)),
                new KeyFrame(Duration.seconds(0.15), new KeyValue(winText.opacityProperty(), 1))
        );
        flicker.setCycleCount(Animation.INDEFINITE);
        flicker.setAutoReverse(true);

        // Hiệu ứng đập nhịp theo “nhạc”
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.4), winText);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.15);
        pulse.setToY(1.15);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);

        // Hiệu ứng “glitch” dịch chuyển
        Timeline glitch = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> winText.setTranslateX(Math.random() * 6 - 3)),
                new KeyFrame(Duration.seconds(0.12), e -> winText.setTranslateX(0))
        );
        glitch.setCycleCount(Animation.INDEFINITE);

        // ====== Hiệu ứng Particle Neon Bay ======
        Color[] neonColors = {
                Color.web("#FF1177"),
                Color.web("#FF44AA"),
                Color.web("#44FFFF"),
                Color.web("#FFDD00")
        };
        Runnable spawnParticle = () -> {
            for (int i = 0; i < 25; i++) {
                Circle p = new Circle(1 + Math.random() * 2, neonColors[(int) (Math.random() * neonColors.length)]);
                p.setTranslateX(0);
                p.setTranslateY(0);
                overlay.getChildren().add(p);
                p.toBack();

                double angle = Math.random() * 2 * Math.PI;
                double distance = 250 + Math.random() * 200;

                TranslateTransition tt = new TranslateTransition(Duration.seconds(2.5 + Math.random()), p);
                tt.setByX(Math.cos(angle) * distance);
                tt.setByY(Math.sin(angle) * distance + Math.random() * 80);

                FadeTransition ft = new FadeTransition(Duration.seconds(2.5), p);
                ft.setFromValue(1);
                ft.setToValue(0);
                Color neonColor = Color.web("#FF1177");
                DropShadow shadow = new DropShadow(45.0, neonColor);
                winText.setEffect(shadow);
                ParallelTransition pt = new ParallelTransition(p, tt, ft);
                pt.setOnFinished(ev -> overlay.getChildren().remove(p));
                pt.play();
            }
        };

        // Gọi particle liên tục
        Timeline particleLoop = new Timeline(
                new KeyFrame(Duration.seconds(0.0), e -> spawnParticle.run()),
                new KeyFrame(Duration.seconds(1.0), e -> spawnParticle.run()),
                new KeyFrame(Duration.seconds(2.0), e -> spawnParticle.run())
        );
        particleLoop.setCycleCount(Animation.INDEFINITE);
        particleLoop.play();

        // ====== Hiệu ứng “Digital Rain” (mưa pixel neon) ======
        Pane rainPane = new Pane();
        overlay.getChildren().add(rainPane);
        rainPane.toBack();

        Timeline rain = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            Rectangle line = new Rectangle(2, 20, Color.web("#00FFFF80"));
            line.setTranslateX(Math.random() * Config.WINDOW_WIDTH);
            line.setTranslateY(-20);
            rainPane.getChildren().add(line);
            TranslateTransition fall = new TranslateTransition(Duration.seconds(1.5 + Math.random()), line);
            fall.setToY(Config.WINDOW_HEIGHT + 20);
            fall.setOnFinished(ev -> rainPane.getChildren().remove(line));
            fall.play();
        }));
        rain.setCycleCount(Animation.INDEFINITE);
        rain.play();

        // ====== Ánh sáng quét (Scanline) ======
        Rectangle scanline = new Rectangle(Config.WINDOW_WIDTH, 10, Color.web("#FFFFFF10"));
        overlay.getChildren().add(scanline);
        scanline.toFront();
        TranslateTransition scan = new TranslateTransition(Duration.seconds(1.5), scanline);
        scan.setFromY(-10);
        scan.setToY(Config.WINDOW_HEIGHT + 10);
        scan.setCycleCount(Animation.INDEFINITE);
        scan.play();

        // ====== Chạy hiệu ứng tổng ======
        fadeIn.play();
        flicker.play();
        pulse.play();
        glitch.play();

        // ====== Chuyển về menu ======
        PauseTransition delay = new PauseTransition(Duration.seconds(8));
        delay.setOnFinished(e -> {
            if (onGameEndToMenu != null) onGameEndToMenu.run();
        });
        delay.play();
    }

}