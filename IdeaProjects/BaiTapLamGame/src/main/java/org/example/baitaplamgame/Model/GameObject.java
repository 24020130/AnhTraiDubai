package org.example.baitaplamgame.Model;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import java.awt.Graphics;

public abstract class GameObject {
    protected double x, y, width, height;
    protected ImageView view;

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();
    public abstract void render(Graphics g);

    public Bounds getBounds() {
        return new BoundingBox(x, y, width, height);
    }

    public void setX(double x) {
        this.x = x;
        if (view != null) view.setX(x);
    }

    public void setY(double y) {
        this.y = y;
        if (view != null) view.setY(y);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public ImageView getView() { return view; }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
