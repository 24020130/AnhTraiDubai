package org.example.baitaplamgame.Model;

import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.PowerUp.BossBullet;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Boss extends Brick {
    private double velocityX = 2.5;
    private long lastShotTime = 0;
    private long lastHitTime = 0;
    private final List<BossBullet> bullets = new ArrayList<>();
    private final Pane root;

    public Boss(double x, double y, Pane root) {
        super(x, y, 360, 240, 30, "boss");
        this.root = root;
        this.hitPoints = 30;

        this.view = new ImageView(ImageLoader.BOSS_IMAGE);
        this.view.setFitWidth(360);
        this.view.setFitHeight(240);

        this.view.setLayoutX(x);
        this.view.setLayoutY(y);

        root.getChildren().add(this.view);
    }

    public void update() {
        x += velocityX;
        if (x <= 0 || x + width >= Config.WINDOW_WIDTH - 220) {
            velocityX *= -1;
        }

        view.setLayoutX(x);
        view.setLayoutY(y);

        long now = System.currentTimeMillis();
        if (now - lastShotTime > 1500) {
            shoot();
            lastShotTime = now;
        }

        bullets.removeIf(b -> {
            b.update();
            if (b.getY() > Config.WINDOW_HEIGHT) {
                root.getChildren().remove(b.getView());
                return true;
            }
            return false;
        });
    }

    @Override
    public void render(Graphics g) {}

    private void shoot() {
        if (bullets.size() > 5) return;
        BossBullet bullet = new BossBullet(x + width / 2 - 5, y + height, root);
        bullets.add(bullet);
    }

    public List<BossBullet> getBullets() {
        return bullets;
    }

    @Override
    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    public void takeDamage() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < 300) return;
        lastHitTime = now;

        hitPoints--;
        System.out.println(hitPoints);
        if (hitPoints <= 0) {
            root.getChildren().remove(view);
        }
    }

    @Override
    public Bounds getBounds() {
        return super.getBounds();
    }
}
