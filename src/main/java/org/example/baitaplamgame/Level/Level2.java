package org.example.baitaplamgame.Level;

import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Level2 extends Level{

    public Level2(int levelNumber) {
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
                System.out.println("Không tìm thấy file level: " + resourcePath);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char c = line.charAt(col);
                    Brick brick = null;

                    switch (c) {
                        case '1' -> brick = new NormalBrick(60 + col * 70, 50 + row * 35, 60, 25, "red");
                        case '2' -> brick = new GreenBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        case '3' -> brick = new FastBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        case '4' -> brick = new MultiBrick(60 + col * 70, 50 + row * 35, 60, 25);
                        case '5' -> brick = new ShrinkPaddle(60 + col * 70, 50 + row * 35, 60, 25);
                    }

                    if (brick != null) {
                        bricks.add(brick);
                        root.getChildren().add(brick.getView());
                    }
                }
                row++;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("✅ Level " + levelNumber + " có " + bricks.size() + " viên gạch sau khi load từ " + resourcePath);
    }

}