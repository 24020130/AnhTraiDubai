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
                        Brick brick = new NormalBrick(60 + col * 70, 50 + row * 35, 60, 25, "red");
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
                        bricks.add(brickes); //4 là MultiBrick thêm 3 Ball
                        root.getChildren().add(brickes.getView());
                    } else if (c =='5') {
                        Brick brick = new ShrinkPaddle(60 + col * 70, 50 + row * 35, 60, 25);
                        bricks.add(brick);
                        root.getChildren().add(brick.getView());
                    }
//                    } else if (c == '6') { // mau xanh la
//                        Brick brick = new NormalBrick(60 +col * 70, 50 + row * 35,60,25,"green1");
//                        bricks.add(brick);
//                        root.getChildren().add(brick.getView());
//                    } else if (c == '7') { // mau xanh bien
//                        Brick brick = new NormalBrick(60 +col * 70, 50 + row * 35,60,25,"Blue");
//                        bricks.add(brick);
//                        root.getChildren().add(brick.getView());
//                    } else if (c == '8') { // mau vang
//                        Brick brick = new NormalBrick(60 +col * 70, 50 + row * 35,60,25,"Yellow");
//                        bricks.add(brick);
//                        root.getChildren().add(brick.getView());
//                    } else if (c == '9') { // mau xanh chuoi
//                        Brick brick = new NormalBrick(60 +col * 70, 50 + row * 35,60,25,"Jade");
//                        bricks.add(brick);
//                        root.getChildren().add(brick.getView());
//                    } else if (c == 'P') { //Purple nha ki hieu thoi
//                        Brick brick = new NormalBrick(60 +col * 70, 50 + row * 35,60,25,"Purple");
//                        bricks.add(brick);
//                        root.getChildren().add(brick.getView());
//                    }else if (c == 'C') { //Orange nha
//                        Brick brick = new NormalBrick(60 +col * 70, 50 + row * 35,60,25,"Orange");
//                        bricks.add(brick);
//                        root.getChildren().add(brick.getView());
//                    }
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
