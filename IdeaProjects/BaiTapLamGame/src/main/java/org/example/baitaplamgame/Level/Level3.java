package org.example.baitaplamgame.Level;

import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Level3 extends Level {
    public List<Brick> bricks = new ArrayList<>();

    public Level3(int levelNumber) {
        super(levelNumber);
    }

    @Override
    public void generateLevelFromFile(String resourcePath, Pane root) {
        for (Brick brick : bricks) root.getChildren().remove(brick.getView());
        bricks.clear();

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.err.println("Không tìm thấy file level: " + resourcePath);
                return;
            }

            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char c = line.charAt(col);
                    Brick brick = null;

                    switch (c) {
                        case '1' -> brick = new NormalBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        case '2' -> brick = new GreenBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        case '3' -> brick = new FastBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        case '4' -> brick = new MultiBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        case '5' -> brick = new HardBrick(60 + col * 70, 50 + row * 35, 60, 25); // Gạch mới, độ cứng cao
                    }

                    if (brick != null) {
                        bricks.add(brick);
                        root.getChildren().add(brick.getView());
                    }
                }
                row++;
            }

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
