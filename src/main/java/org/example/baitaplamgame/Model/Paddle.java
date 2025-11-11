package org.example.baitaplamgame.Model;

import javafx.animation.FadeTransition;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.Config;

public class Paddle extends MovableObject {
    private double speed;
    private PowerUp currentPowerUp;
    private final ImageView view;
    private Ball ball;
    private GameManager gameManager;
    public Paddle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;
        view = new ImageView(ImageLoader.PADDLE_IMAGE);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setX(x);
        view.setY(y);
    }
    public Paddle(double x, double y, double width, double height, double speed, GameManager gameManager) {
        this(x, y, width, height, speed);
        this.gameManager = gameManager;
    }


    @Override
    public void update() {
        updateView();
        createTrail();
    }

    public void moveLeft() {
        x = Math.max(0, x - speed);
        updateView();
    }

    public void moveRight() {
        x = Math.min(Config.WINDOW_WIDTH - width - 220, x + speed);
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
    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
