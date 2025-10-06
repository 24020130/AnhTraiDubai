package org.example.baitaplamgame.Model;

import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Utlis.ImageLoader;

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
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setX(x);
        view.setY(y);
    }

    public void takeHit() {
        hitPoints--;
        if (isDestroyed()) {
            view.setVisible(false);
        }
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
