package org.example.baitaplamgame.Model;

import java.awt.*;

public class MovableObject extends GameObject{
    public double dx;
    public double dy;
    public MovableObject(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
    public void move(){
        x += dx;
        y += dy;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {

    }

}
