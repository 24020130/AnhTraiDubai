package org.example.baitaplamgame.PowerUp;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.baitaplamgame.Model.Paddle;
import org.example.baitaplamgame.Utlis.ImageLoader;

import java.util.Random;

public class BossFireBall {
    private double x, y;
    private double dx, dy;
    private double speed = 1;
    private ImageView view;
    private Pane root;
    private Timeline particleEffect;

    private OnHitListener hitListener;

    public interface OnHitListener {
        void onHit();
    }

    public void setOnHitListener(OnHitListener listener) {
        this.hitListener = listener;
    }

    private static final Random random = new Random();

    private static Image loadImage(String path) {
        var stream = ImageLoader.class.getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Không tìm thấy ảnh: " + path);
            return null;
        }
        return new Image(stream);
    }

    public BossFireBall(double x, double y, double angle, Pane root) {
        this.x = x;
        this.y = y;
        this.root = root;

        // Tính vector bay dựa theo góc
        double radians = Math.toRadians(angle);
        dx = Math.cos(radians) * speed;
        dy = Math.sin(radians) * speed;

        Image fireImg = loadImage("/images/fireball_big.png");
        if (fireImg == null)
            fireImg = loadImage("/images/boss_bullet.png");

        view = new ImageView(fireImg);
        view.setFitWidth(50);
        view.setFitHeight(50);
        view.setLayoutX(x - 45);
        view.setLayoutY(y - 45);

        // Ánh sáng cầu lửa
        Glow glow = new Glow(0.9);
        Bloom bloom = new Bloom(0.7);
        glow.setInput(bloom);
        view.setEffect(glow);

        root.getChildren().add(view);

        startParticles();
        startAnimations();
    }

    private void startAnimations() {
        RotateTransition rotate = new RotateTransition(Duration.seconds(2), view);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.play();

        FadeTransition fade = new FadeTransition(Duration.seconds(0.6), view);
        fade.setFromValue(1.0);
        fade.setToValue(0.6);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
    }

    private void startParticles() {
        particleEffect = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            Circle flame = new Circle(5 + random.nextDouble() * 10, Color.ORANGE);
            flame.setLayoutX(view.getLayoutX() + 45 + random.nextInt(20) - 10);
            flame.setLayoutY(view.getLayoutY() + 60);

            root.getChildren().add(flame);

            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), flame);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setOnFinished(ev -> root.getChildren().remove(flame));
            fade.play();
        }));
        particleEffect.setCycleCount(Animation.INDEFINITE);
        particleEffect.play();
    }

    public void update() {
        x += dx;
        y += dy;
        view.setLayoutX(x - 45);
        view.setLayoutY(y - 45);

        // Xóa nếu ra khỏi màn hình
        if (x < -100 || x > root.getWidth() + 100 || y > root.getHeight() + 150) {
            destroy();
        }
    }

    public boolean collidesWith(Paddle paddle) {
        if (getBounds().intersects(paddle.getView().getBoundsInParent())) {
            if (hitListener != null) hitListener.onHit(); // trừ máu
            explode();
            return true;
        }
        return false;
    }

    public Bounds getBounds() {
        return view.getBoundsInParent();
    }

    public void explode() {
        if (particleEffect != null) particleEffect.stop();

        Circle explosion = new Circle(view.getLayoutX() + 45, view.getLayoutY() + 45, 20, Color.RED);
        root.getChildren().add(explosion);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.3), explosion);
        scale.setToX(5);
        scale.setToY(5);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), explosion);
        fade.setFromValue(1);
        fade.setToValue(0);

        ParallelTransition boom = new ParallelTransition(scale, fade);
        boom.setOnFinished(e -> root.getChildren().remove(explosion));
        boom.play();

        root.getChildren().remove(view);
    }

    public void destroy() {
        if (particleEffect != null) particleEffect.stop();
        root.getChildren().remove(view);
    }

    public ImageView getView() {
        return view;
    }
}
