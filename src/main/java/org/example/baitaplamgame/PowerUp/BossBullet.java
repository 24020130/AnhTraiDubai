package org.example.baitaplamgame.PowerUp;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.Paddle;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;

import javax.swing.*;
import java.nio.FloatBuffer;

public class BossBullet {
    private double x, y;
    private double speed = 1.5;
    private ImageView view;
    private Pane root;
    private static Image loadImage(String path) {
        var stream = ImageLoader.class.getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Không tìm thấy ảnh: " + path);
            return null;
        }
        return new Image(stream);
    }

    public BossBullet(double x, double y, Pane root) {
        this.x = x;
        this.y = y;
        this.root = root;
        Image bulletImg = loadImage("/images/boss_bullet.png");
        view = new ImageView(bulletImg);
        view.setFitWidth(30);
        view.setFitHeight(70);
        view.setLayoutX(x);
        view.setLayoutY(y);
        root.getChildren().add(view);
    }

    public void update() {
        y += speed;
        view.setLayoutY(y);
    }

    public ImageView getView() {
        return view;
    }

    public double getY() {
        return y;
    }

    public Bounds getBounds() {
        return view.getBoundsInParent();
    }

    public boolean collidesWith(Paddle paddle) {
        return this.getBounds().intersects(paddle.getView().getBoundsInParent());
    }

}
