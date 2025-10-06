package org.example.baitaplamgame.Level;

import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.Brick;
import org.example.baitaplamgame.Model.NormalBrick;
import org.example.baitaplamgame.Model.StrongBrick;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private int levelNumber;
    private List<Brick> bricks = new ArrayList<>();

    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public void generateLevel(int num, Pane root) {
        for (Brick brick : bricks) {
            root.getChildren().remove(brick.getView());
        }
        bricks.clear();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                Brick brick = (row % 2 == 0)
                        ? new NormalBrick(60 + col * 70, 50 + row * 35, 60, 25)
                        : new StrongBrick(60 + col * 70, 50 + row * 35, 60, 25);
                bricks.add(brick);
                root.getChildren().add(brick.getView());
            }
        }
    }




    public List<Brick> getBricks() { return bricks; }
}
