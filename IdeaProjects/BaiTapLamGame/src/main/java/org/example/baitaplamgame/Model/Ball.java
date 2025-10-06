package org.example.baitaplamgame.Model;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.baitaplamgame.Utlis.ImageLoader;

public class Ball extends MovableObject {
    private double speed;
    private double velocityX, velocityY;
    private final ImageView view;

    public Ball(double x, double y, double size, double speed) {
        super(x, y, size, size);
        this.speed = speed;

        // Khởi đầu đi chéo lên trên bên phải
        double angle = Math.toRadians(-45);
        this.velocityX = speed * Math.cos(angle);
        this.velocityY = speed * Math.sin(angle);

        view = new ImageView(ImageLoader.BALL_IMAGE);
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setX(x);
        view.setY(y);
    }

    @Override
    public void update() {
        x += velocityX;
        y += velocityY;
        updateView();
        createTrail();
    }

    private void updateView() {
        view.setX(x);
        view.setY(y);
    }

    private void createTrail() {
        Pane parent = (Pane) view.getParent();
        if (parent == null) return;

        Circle trail = new Circle(x + width / 2, y + height / 2, 6);
        double hue = Math.random() * 360;
        trail.setFill(Color.hsb(hue, 1.0, 1.0, 0.4));
        parent.getChildren().add(trail);

        FadeTransition fade = new FadeTransition(Duration.millis(400), trail);
        fade.setFromValue(0.8);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> parent.getChildren().remove(trail));
        fade.play();
    }

    public void createRainbowExplosion(double centerX, double centerY) {
        Pane parent = (Pane) view.getParent();
        if (parent == null) return;

        for (int i = 0; i < 10; i++) {
            Circle spark = new Circle(centerX, centerY, 4);
            double hue = Math.random() * 360;
            spark.setFill(Color.hsb(hue, 1.0, 1.0, 0.8));
            parent.getChildren().add(spark);

            double dx = (Math.random() - 0.5) * 8;
            double dy = (Math.random() - 0.5) * 8;

            TranslateTransition move = new TranslateTransition(Duration.millis(500), spark);
            move.setByX(dx * 15);
            move.setByY(dy * 15);

            FadeTransition fade = new FadeTransition(Duration.millis(500), spark);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            ParallelTransition combo = new ParallelTransition(move, fade);
            combo.setOnFinished(e -> parent.getChildren().remove(spark));
            combo.play();
        }
    }

    public Bounds getBounds() {
        return view.getBoundsInParent();
    }

    public ImageView getView() {
        return view;
    }

    // 🔹 Bật lại khi chạm tường
    public void checkCollisionWithWalls(double sceneWidth, double sceneHeight) {
        if (x <= 0 || x + width >= sceneWidth) {
            velocityX *= -1;
            createRainbowExplosion(x + width / 2, y + height / 2);
        }
        if (y <= 0) {
            velocityY *= -1;
            createRainbowExplosion(x + width / 2, y + height / 2);
        }
    }

    // Bật lại khi va chạm với vật thể (Player / Brick)
    public void bounceOff(GameObject obj) {
        Bounds ballBounds = this.getBounds();
        Bounds objBounds = obj.getBounds();

        if (ballBounds == null || objBounds == null) return;

        double ballCenterX = ballBounds.getMinX() + ballBounds.getWidth() / 2;
        double ballCenterY = ballBounds.getMinY() + ballBounds.getHeight() / 2;
        double objCenterX = objBounds.getMinX() + objBounds.getWidth() / 2;
        double objCenterY = objBounds.getMinY() + objBounds.getHeight() / 2;

        double dx = ballCenterX - objCenterX;
        double dy = ballCenterY - objCenterY;

        if (Math.abs(dx) > Math.abs(dy)) velocityX *= -1;
        else velocityY *= -1;

        createRainbowExplosion(ballCenterX, ballCenterY);
    }

    // 🔹 Cho phép chỉnh vận tốc theo góc paddle
    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    public double getSpeed() {
        return speed;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }
}
