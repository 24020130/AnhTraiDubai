package org.example.baitaplamgame.Model;

import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.baitaplamgame.Utlis.ImageLoader;

public abstract class PowerUp extends GameObject {
    protected String type;
    protected double speed = 2;
    protected ImageView view;
    protected boolean collected = false;

    public PowerUp(double x, double y, String type) {
        super(x, y, 30, 30);
        this.type = type;

        view = new ImageView(ImageLoader.ITEM_IMAGE);
        view.setFitWidth(30);
        view.setFitHeight(30);
        view.setX(x);
        view.setY(y);

        // Hiệu ứng rơi xuống
        TranslateTransition fall = new TranslateTransition(Duration.seconds(5), view);
        fall.setByY(500);
        fall.setCycleCount(1);
        fall.play();
    }

    @Override
    public void update() {
        y += speed;
        view.setY(y);
    }

    @Override
    public void render(java.awt.Graphics g) {}

    public ImageView getView() {
        return view;
    }

    public String getType() {
        return type;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    // Các phương thức trừu tượng
    public abstract void applyEffect(Paddle paddle);
    public abstract void removeEffect(Paddle paddle);
}
