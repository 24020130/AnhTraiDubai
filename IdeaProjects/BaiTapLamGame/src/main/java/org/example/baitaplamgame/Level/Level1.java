package org.example.baitaplamgame.Level;

import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Level1 extends Level{
    public List<Brick> bricks = new ArrayList<>();
    public Level1(int levelNumber) {
        super(levelNumber);
    }

    @Override
    public void generateLevelFromFile(String resourcePath, Pane root) {
        for (Brick brick : bricks) {
            root.getChildren().remove(brick.getView());
        }
        bricks.clear();

        try {
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char c = line.charAt(col);
                    if (c == '1') {
                        Brick brick = new NormalBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        bricks.add(brick);
                        root.getChildren().add(brick.getView());
                    } else if (c == '2') {
                        Brick brick = new GreenBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        bricks.add(brick);
                        root.getChildren().add(brick.getView());
                    }
                    else if(c == '3'){
                        Brick brick = new FastBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        bricks.add(brick);
                        root.getChildren().add(brick.getView());
                    } else if ( c == '4') {
                        Brick brickes = new MultiBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        bricks.add(brickes);
                        root.getChildren().add(brickes.getView());
                    }
                }
                row++;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public void setBricks(List<Brick> bricks) {
        this.bricks = bricks;
    }
}
