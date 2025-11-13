package org.example.baitaplamgame.Model;

import javafx.scene.image.ImageView;

import java.awt.*;

public class DummyBrick extends Brick {
    public DummyBrick(double x, double y) {
        super(x, y, 1, 1, 1, "dummy");
        this.view = new ImageView();
        this.view.setVisible(false);
    }

    @Override
    public void render(Graphics g) {

    }
}
