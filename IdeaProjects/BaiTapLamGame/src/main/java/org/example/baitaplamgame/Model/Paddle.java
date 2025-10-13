package org.example.baitaplamgame.Model;

import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.Config;

public class Paddle extends MovableObject {
    private double speed;
    private PowerUp currentPowerUp;
    private final ImageView view;
    private Ball ball;

    public Paddle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;


        view = new ImageView(ImageLoader.PADDLE_IMAGE);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setX(x);
        view.setY(y);
    }

    @Override
    public void update() {
        updateView();
    }

    public void moveLeft() {
        x = Math.max(0, x - speed);
        updateView();
    }

    public void moveRight() {
        x = Math.min(Config.WINDOW_WIDTH - width, x + speed);
        updateView();
    }

    private void updateView() {
        view.setX(x);
        view.setY(y);
        view.setFitWidth(width);
        view.setFitHeight(height);
    }
    @Override
    public double getX() { return x; }
    @Override
    public double getY() { return y; }

    public double getWidth() {
        return width;
    }

    public void setWidth(double newWidth) {
        double center = x + width / 2.0;
        this.width = newWidth;
        this.x = center - newWidth / 2.0;
        if (x < 0) x = 0;
        if (x + width > Config.WINDOW_WIDTH) x = Config.WINDOW_WIDTH - width;
        updateView();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double newHeight) {
        this.height = newHeight;
        updateView();
    }

    public ImageView getView() {
        return view;
    }

    public Bounds getBounds() {
        return view.getBoundsInParent();
    }

    public Ball getBall(){
        return this.ball;
    }


    public void setBall(Ball ball) {
        this.ball = ball;
    }




}
