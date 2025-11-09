package org.example.baitaplamgame.Model;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.Config;

public class Paddle extends MovableObject {
    private double speed;
    private final ImageView view;
    private Ball ball;
    private GameManager gameManager;

    // 🌈 Hiệu ứng ánh sáng động
    private final DropShadow glow;
    private final MotionBlur motionBlur;
    private double hueShift = 0;
    private long lastTrailTime = 0;
    private double lastX = 0;

    public Paddle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;

        // Hình ảnh paddle
        view = new ImageView(ImageLoader.PADDLE_IMAGE);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setX(x);
        view.setY(y);

        // Hiệu ứng ánh sáng
        glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(30);
        glow.setSpread(0.6);

        // Làm mờ chuyển động
        motionBlur = new MotionBlur(0, 0);
        motionBlur.setRadius(0);

        // Kết hợp hiệu ứng
        glow.setInput(motionBlur);
        view.setEffect(glow);

        // Hiệu ứng đổi màu ánh sáng
        Timeline hueTimeline = new Timeline(
                new KeyFrame(Duration.millis(50), e -> updateNeonColor())
        );
        hueTimeline.setCycleCount(Animation.INDEFINITE);
        hueTimeline.play();
    }

    private void updateNeonColor() {
        hueShift += 0.6;
        if (hueShift > 360) hueShift = 0;
        glow.setColor(Color.hsb(hueShift, 1.0, 1.0));
    }

    public void moveLeft() {
        double delta = -speed;
        x = Math.max(0, x + delta);
        updateView();
        spawnEnergyTrail(delta);
        animateMotion(delta);
        lastX = x;
    }

    public void moveRight() {
        double delta = speed;
        x = Math.min(Config.WINDOW_WIDTH - width - 220, x + delta);
        updateView();
        spawnEnergyTrail(delta);
        animateMotion(delta);
        lastX = x;
    }

    /** 🌀 Hiệu ứng mượt khi paddle di chuyển */
    private void animateMotion(double delta) {
        double intensity = Math.min(1.0, Math.abs(delta) / 10);
        motionBlur.setAngle(delta > 0 ? 0 : 180);
        motionBlur.setRadius(10 * intensity);

        // Làm hiệu ứng giảm dần blur sau khi dừng
        Timeline resetBlur = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(motionBlur.radiusProperty(), 0, Interpolator.EASE_OUT))
        );
        resetBlur.play();
    }

    private void updateView() {
        view.setX(x);
        view.setY(y);
    }

    /** 🌠 Vệt sáng năng lượng sau lưng paddle */
    private void spawnEnergyTrail(double delta) {
        if (Math.abs(delta) < 1.5) return;

        Pane parent = (Pane) view.getParent();
        if (parent == null) return;

        long now = System.currentTimeMillis();
        if (now - lastTrailTime < 40) return;
        lastTrailTime = now;

        // Dải sáng động
        Rectangle trail = new Rectangle(x, y, width, height);
        trail.setArcWidth(40);
        trail.setArcHeight(40);

        Stop[] stops = new Stop[]{
                new Stop(0, Color.hsb(hueShift, 1, 1, 0.6)),
                new Stop(1, Color.hsb((hueShift + 60) % 360, 1, 1, 0.0))
        };
        trail.setFill(new LinearGradient(
                delta > 0 ? 0 : 1, 0, delta > 0 ? 1 : 0, 0, true,
                CycleMethod.NO_CYCLE, stops
        ));
        trail.setEffect(new GaussianBlur(18));

        parent.getChildren().add(trail);

        // Hiệu ứng tan biến mượt
        FadeTransition fade = new FadeTransition(Duration.millis(400), trail);
        fade.setFromValue(0.7);
        fade.setToValue(0.0);

        TranslateTransition move = new TranslateTransition(Duration.millis(400), trail);
        move.setByX(delta > 0 ? 25 : -25);

        ScaleTransition scale = new ScaleTransition(Duration.millis(400), trail);
        scale.setToX(1.2);
        scale.setToY(0.8);

        ParallelTransition effect = new ParallelTransition(fade, move, scale);
        effect.setOnFinished(e -> parent.getChildren().remove(trail));
        effect.play();
    }

    /** ✨ Hiệu ứng va chạm bóng */
    public void hitEffect() {
        Pane parent = (Pane) view.getParent();
        if (parent == null) return;

        Circle burst = new Circle(x + width / 2, y + height / 2, 8,
                Color.hsb(hueShift, 1, 1, 0.8));
        burst.setEffect(new Bloom(0.8));
        parent.getChildren().add(burst);

        ScaleTransition expand = new ScaleTransition(Duration.millis(200), burst);
        expand.setFromX(1);
        expand.setToX(3);
        expand.setFromY(1);
        expand.setToY(3);

        FadeTransition fade = new FadeTransition(Duration.millis(200), burst);
        fade.setFromValue(0.8);
        fade.setToValue(0.0);

        ParallelTransition fx = new ParallelTransition(expand, fade);
        fx.setOnFinished(e -> parent.getChildren().remove(burst));
        fx.play();
    }

    @Override
    public void update() {
        updateView();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }

    public void setWidth(double newWidth) {
        double center = x + width / 2.0;
        this.width = newWidth;
        this.x = center - newWidth / 2.0;
        if (x < 0) x = 0;
        if (x + width > Config.WINDOW_WIDTH) x = Config.WINDOW_WIDTH - width;
        updateView();
    }

    public double getHeight() { return height; }
    public void setHeight(double newHeight) {
        this.height = newHeight;
        updateView();
    }

    public ImageView getView() { return view; }
    public Bounds getBounds() { return view.getBoundsInParent(); }

    public Ball getBall() { return ball; }
    public void setBall(Ball ball) { this.ball = ball; }

    public GameManager getGameManager() { return gameManager; }
    public void setGameManager(GameManager gm) { this.gameManager = gm; }
}
