package org.example.baitaplamgame.Model;

import javafx.animation.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.*;

public class BrickGroup {
    private List<Brick> bricks;
    private Pane root;
    private String pattern; // "circle", "wave", "zigzag"
    private double centerX, centerY;
    private double radius = 120;
    private Random random = new Random();

    public BrickGroup(Pane root, String pattern, double centerX, double centerY) {
        this.bricks = new ArrayList<>();
        this.root = root;
        this.pattern = pattern;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void addBrick(Brick brick) {
        bricks.add(brick);
        root.getChildren().add(brick.getView());
    }

    /** Bắt đầu chuyển động theo mẫu */
    public void startPattern() {
        Timeline patternMotion = new Timeline(
                new KeyFrame(Duration.millis(30), e -> updatePattern())
        );
        patternMotion.setCycleCount(Animation.INDEFINITE);
        patternMotion.play();

        Timeline dropTimer = new Timeline(
                new KeyFrame(Duration.seconds(3 + random.nextDouble() * 2), e -> dropRandomBrick())
        );
        dropTimer.setCycleCount(Animation.INDEFINITE);
        dropTimer.play();
    }

    /** Cập nhật vị trí từng viên theo mẫu */
    private void updatePattern() {
        double t = System.currentTimeMillis() / 500.0;

        for (int i = 0; i < bricks.size(); i++) {
            Brick b = bricks.get(i);
            if (b.isDestroyed()) continue;

            double x, y;
            switch (pattern) {
                case "circle" -> {
                    double angle = (2 * Math.PI / bricks.size()) * i + t * 0.2;
                    x = centerX + Math.cos(angle) * radius;
                    y = centerY + Math.sin(angle) * radius;
                }
                case "wave" -> {
                    x = centerX + i * 60 - bricks.size() * 30;
                    y = centerY + Math.sin(t + i * 0.6) * 50;
                }
                case "zigzag" -> {
                    x = centerX + (i % 5) * 60 - 150;
                    y = centerY + ((i / 5) % 2 == 0 ? 0 : 50) + Math.sin(t + i) * 10;
                }
                default -> {
                    x = b.getView().getX();
                    y = b.getView().getY();
                }
            }
            b.getView().setX(x);
            b.getView().setY(y);
        }
    }

    /** Làm rơi ngẫu nhiên 1 viên */
    private void dropRandomBrick() {
        if (bricks.isEmpty()) return;

        Brick chosen = bricks.get(random.nextInt(bricks.size()));
        if (chosen.isDestroyed() || chosen.getView() == null) return;

        chosen.fall();
    }
}
