package org.example.baitaplamgame.Level;

import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.Brick;
import org.example.baitaplamgame.Model.GreenBrick;
import org.example.baitaplamgame.Model.NormalBrick;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class Level {
    protected int levelNumber;
    protected List<Brick> bricks = new ArrayList<>();

    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
    }
    public abstract void generateLevelFromFile(String resourcePath, Pane root);

    public List<Brick> getBricks() {
        return bricks;
    }

    public void setBricks(List<Brick> bricks) {
        this.bricks = bricks;
    }
}
