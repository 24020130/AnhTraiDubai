package org.example.baitaplamgame.Model;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Utlis.ImageLoader;

import javafx.util.Duration;

public abstract class Brick extends GameObject {
    protected int hitPoints;
    protected String type;

    public Brick(double x, double y, double width, double height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;

        if (type.equalsIgnoreCase("red")) {
            view = new ImageView(ImageLoader.BRICK_IMAGE);
        } else if(type.equalsIgnoreCase("green")){
            view = new ImageView(ImageLoader.BRICK_GREEN_IMAGE);
        }
        else if(type.equalsIgnoreCase("fast")){
            view = new ImageView(ImageLoader.BRICK_FAST_IMAGE);
        } else if (type.equalsIgnoreCase("multibrick")) {
            view = new ImageView(ImageLoader.MULTI_BALL_BRICK);
        }
        else if (type.equalsIgnoreCase("hard")) { // ✅ thêm dòng này
            view = new ImageView(ImageLoader.HARD_BRICK_IMAGE);
        }
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setX(x);
        view.setY(y);
    }

    public void takeHit() {
        hitPoints--;
        playShakeEffect();
        if (isDestroyed()) {
            view.setVisible(false);
        }
    }
    protected void playShakeEffect() {
        double originalX = view.getX();
        Timeline shake = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(view.xProperty(), originalX)),
                new KeyFrame(Duration.millis(50), new KeyValue(view.xProperty(), originalX - 5)),
                new KeyFrame(Duration.millis(100), new KeyValue(view.xProperty(), originalX + 5)),
                new KeyFrame(Duration.millis(150), new KeyValue(view.xProperty(), originalX - 3)),
                new KeyFrame(Duration.millis(200), new KeyValue(view.xProperty(), originalX + 3)),
                new KeyFrame(Duration.millis(250), new KeyValue(view.xProperty(), originalX))
        );

        FadeTransition fade = new FadeTransition(Duration.millis(300), view);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        shake.setOnFinished(e -> fade.play());
        fade.setOnFinished(e -> view.setVisible(false));

        shake.play();
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    @Override
    public void update() { }

    @Override
    public ImageView getView() {
        return view;
    }

    public String getType() {
        return type;
    }
}
