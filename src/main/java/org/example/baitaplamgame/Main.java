package org.example.baitaplamgame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.baitaplamgame.Model.*;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.SoundManager; // 👉 thêm dòng này


import java.util.*;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.baitaplamgame.Model.*;
import org.example.baitaplamgame.Utlis.ImageLoader;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        double width = Config.WINDOW_WIDTH;
        double height = Config.WINDOW_HEIGHT;

        Pane root = new Pane();
        GameManager gameManager = new GameManager(root, width, height);
        gameManager.startGame();
        SoundManager.playBackground("background.mp3");

        Scene scene = new Scene(root, width, height);
        gameManager.setupInput(scene);

        stage.setTitle("Brick Breaker Demo");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}

