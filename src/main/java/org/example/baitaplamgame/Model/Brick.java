package org.example.baitaplamgame.Model;

import javafx.animation.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.baitaplamgame.Utlis.ImageLoader;

public abstract class Brick extends GameObject {
    protected int hitPoints;
    protected String type;
    private long lastHitTime = 0;
    private boolean isFalling = false;

    public Brick(double x, double y, double width, double height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;

        if (type == null || type.equalsIgnoreCase("boss") || type.equalsIgnoreCase("dummy")) {
            this.view = null;
            return;
        }

        switch (type.toLowerCase()) {
            case "red" -> view = new ImageView(ImageLoader.BRICK_IMAGE);
            case "green" -> view = new ImageView(ImageLoader.BRICK_GREEN_IMAGE);
            case "fast" -> view = new ImageView(ImageLoader.BRICK_FAST_IMAGE);
            case "multibrick" -> view = new ImageView(ImageLoader.MULTI_BALL_BRICK);
            case "shirkpaddle" -> view = new ImageView(ImageLoader.BRICK_SHIRK_PADDLE);
            default -> view = new ImageView(ImageLoader.BRICK_IMAGE);
        }

        if (view != null) {
            view.setFitWidth(width);
            view.setFitHeight(height);
            view.setX(x);
            view.setY(y);

            // 🌈 Hiệu ứng ánh sáng neon
            DropShadow neon = new DropShadow(20, Color.web(getNeonColor(type)));
            neon.setSpread(0.6);
            view.setEffect(neon);

            // 💡 Hiệu ứng sáng mờ nhẹ
            Timeline pulse = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(view.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(1.5), new KeyValue(view.opacityProperty(), 0.7)),
                    new KeyFrame(Duration.seconds(3.0), new KeyValue(view.opacityProperty(), 1.0))
            );
            pulse.setCycleCount(Animation.INDEFINITE);
            pulse.play();
        }
    }

    private String getNeonColor(String type) {
        return switch (type.toLowerCase()) {
            case "red" -> "#ff2e63";
            case "green" -> "#08d9d6";
            case "fast" -> "#ffe400";
            case "multibrick" -> "#f542dd";
            case "shirkpaddle" -> "#00fff0";
            default -> "#ffffff";
        };
    }

    public void takeHit() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < 100) return;
        lastHitTime = now;

        hitPoints--;
        if (hitPoints <= 0) playDestroyEffect();
    }

    /** Hiệu ứng vỡ neon + fade out */
    private void playDestroyEffect() {
        double originalX = view.getX();

        Timeline shake = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(view.xProperty(), originalX)),
                new KeyFrame(Duration.millis(50), new KeyValue(view.xProperty(), originalX - 5)),
                new KeyFrame(Duration.millis(100), new KeyValue(view.xProperty(), originalX + 5)),
                new KeyFrame(Duration.millis(150), new KeyValue(view.xProperty(), originalX - 3)),
                new KeyFrame(Duration.millis(200), new KeyValue(view.xProperty(), originalX + 3)),
                new KeyFrame(Duration.millis(250), new KeyValue(view.xProperty(), originalX))
        );

        FadeTransition fade = new FadeTransition(Duration.millis(400), view);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        shake.setOnFinished(e -> fade.play());
        fade.setOnFinished(e -> view.setVisible(false));

        shake.play();
    }

    /** 🌠 Làm viên gạch rơi xuống (được gọi từ BrickGroup) */
    public void fall() {
        if (isFalling || view == null) return;
        isFalling = true;

        double startY = view.getY();
        double endY = startY + 600 + Math.random() * 200; // rơi xa ngẫu nhiên
        double rotateSpeed = (Math.random() > 0.5 ? 360 : -360);

        Timeline fallAnim = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(view.yProperty(), startY),
                        new KeyValue(view.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(2.5),
                        new KeyValue(view.yProperty(), endY, Interpolator.EASE_IN),
                        new KeyValue(view.rotateProperty(), rotateSpeed, Interpolator.EASE_IN))
        );

        FadeTransition fade = new FadeTransition(Duration.seconds(2.5), view);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        ParallelTransition fallEffect = new ParallelTransition(fallAnim, fade);
        fallEffect.setOnFinished(e -> {
            view.setVisible(false);
            isFalling = false;
        });
        fallEffect.play();
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    @Override
    public void update() {}

    @Override
    public ImageView getView() {
        return view;
    }

    public String getType() {
        return type;
    }
}
