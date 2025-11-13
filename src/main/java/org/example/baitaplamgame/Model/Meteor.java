package org.example.baitaplamgame.Model;

import javafx.animation.FadeTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;


public class Meteor extends ImageView {
    private double speedX;
    private double speedY;
    private boolean active = true;

    public Meteor(double startX, double startY) {
        // Ảnh thiên thạch
        setImage(new Image(getClass().getResource("/images/meteor.png").toExternalForm()));

        // Hiệu ứng cháy sáng
        DropShadow glow = new DropShadow();
        glow.setColor(Color.ORANGERED);
        glow.setRadius(30);
        setEffect(glow);

        // Hiệu ứng nhấp nháy
        FadeTransition flicker = new FadeTransition(Duration.seconds(0.2), this);
        flicker.setFromValue(1.0);
        flicker.setToValue(0.6);
        flicker.setCycleCount(FadeTransition.INDEFINITE);
        flicker.setAutoReverse(true);
        flicker.play();

        setFitWidth(40);
        setPreserveRatio(true);
        setLayoutX(startX);
        setLayoutY(startY);

        // random hướng rơi
        this.speedX = (Math.random() - 0.5) * 3;
        this.speedY = 3 + Math.random() * 3;
    }

    public void update(Pane gamePane) {
        if (!active) return;

        setLayoutX(getLayoutX() + speedX);
        setLayoutY(getLayoutY() + speedY);

        // Nếu rơi ra ngoài màn
        if (getLayoutY() > gamePane.getHeight()) {
            active = false;
            gamePane.getChildren().remove(this);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void destroy(Pane gamePane) {
        if (!active) return;
        active = false;
        playExplosion(gamePane);

        // ✅ Xóa meteor sau animation 0.5s
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(e -> gamePane.getChildren().remove(this));
        delay.play();
    }


    private void playExplosion(Pane gamePane) {
        AudioClip explosionSound = new AudioClip(getClass().getResource("/sounds/explosion.mp3").toExternalForm());
        explosionSound.play();

        ImageView boom = new ImageView(new Image(getClass().getResource("/images/explosion.png").toExternalForm()));
        boom.setFitWidth(60);
        boom.setPreserveRatio(true);
        boom.setLayoutX(getLayoutX());
        boom.setLayoutY(getLayoutY());
        gamePane.getChildren().add(boom);

        // Hiệu ứng mờ dần
        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), boom);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> gamePane.getChildren().remove(boom));
        ft.play();

        // 🌋 Hiệu ứng rung màn hình
        shakeScreen(gamePane);
    }
    private void shakeScreen(Pane gamePane) {
        double intensity = 8; // độ mạnh của rung
        int count = 8;        // số lần rung

        javafx.animation.Timeline shake = new javafx.animation.Timeline();
        for (int i = 0; i < count; i++) {
            double dx = (Math.random() - 0.5) * intensity;
            double dy = (Math.random() - 0.5) * intensity;
            shake.getKeyFrames().add(new javafx.animation.KeyFrame(
                    Duration.millis(i * 30),
                    new javafx.animation.KeyValue(gamePane.translateXProperty(), dx),
                    new javafx.animation.KeyValue(gamePane.translateYProperty(), dy)
            ));
        }

        shake.getKeyFrames().add(new javafx.animation.KeyFrame(
                Duration.millis(count * 30),
                new javafx.animation.KeyValue(gamePane.translateXProperty(), 0),
                new javafx.animation.KeyValue(gamePane.translateYProperty(), 0)
        ));

        shake.play();
    }

}
