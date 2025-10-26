package org.example.baitaplamgame.Model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SupportPaddle extends MovableObject {
    private final ImageView view;
    private ImageView spamLaze;
    private double speed = 2.5;
    private int direction = 1; // 1 = sang phải, -1 = sang trái
    private final List<Laze> lazeList = new ArrayList<>();

    private long lastTurnTime = 0;
    private long nextTurnDelay = getRandomDelay();

    public SupportPaddle(double startX, double startY, double width, double height) {
        super(startX, startY, width, height);
        view = new ImageView(ImageLoader.SUPPORT_RIGHT);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setX(x);
        view.setY(y);
    }

    private long getRandomDelay() {
        // random thời gian đổi hướng giây
        return 500 + (long) (Math.random() * 1000);
    }

    @Override
    public void update() {
        // di chuyển ngang
        x += speed * direction;

        // giới hạn trong màn hình
        if (x <= 0) {
            x = 0;
            direction = 1;
            resetTimer();

        } else if (x + width >= Config.WINDOW_WIDTH - 220) { // trừ khung phải nếu có HUD
            x = Config.WINDOW_WIDTH - 220 - width;
            direction = -1;
            resetTimer();
        }

        // random đổi hướng theo thời gian
        long now = System.currentTimeMillis();
        if (now - lastTurnTime > nextTurnDelay) {
            direction *= -1;
            spawmGun();
            resetTimer();
        }

        view.setX(x);
        view.setY(y);

        Iterator<Laze> iterator =  lazeList.iterator();
        while(iterator.hasNext()) {
            Laze l = iterator.next();
            l.update();
            if (l.isOutOfScreen()) iterator.remove();
        }


    }

    public void spawmGun() {
       double lx = view.getX() + width/2 - 15;
       double ly = view.getY() -40;
       lazeList.add(new Laze(lx,ly,40,50));

    }
    private void resetTimer() {
        lastTurnTime = System.currentTimeMillis();
        nextTurnDelay = getRandomDelay();
    }

    public ImageView getView() {
        return view;
    }
    public List<Laze> getLazeList() {
        return lazeList;
    }

}
