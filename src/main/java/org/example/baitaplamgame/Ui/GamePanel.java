package org.example.baitaplamgame.Ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ScoreFileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GamePanel extends Pane {
    private Runnable onStart;

    public void setOnStart(Runnable onStart) {
        this.onStart = onStart;
    }

    public GamePanel() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        Background background = new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, null,
                        new Stop[]{
                                new Stop(0, Color.web("#2d0000")),
                                new Stop(1, Color.web("#6b1000"))
                        }),
                CornerRadii.EMPTY,
                Insets.EMPTY
        ));
        container.setBackground(background);

        container.setBorder(new Border(new BorderStroke(
                Color.web("#ff6600"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(15),
                new BorderWidths(3)
        )));

        DropShadow glow = new DropShadow(20, Color.ORANGE);
        glow.setSpread(0.3);
        container.setEffect(glow);

        // Tiêu đề
        Text titleText = new Text("MAIN MENU");
        titleText.setFill(Color.web("#ff9900"));
        titleText.setFont(Font.font("Consolas", 28));

        // Các nút
        Button btnStart = createButton("START GAME");
        Button btnSettings = createButton("SETTINGS");
        Button btnExit = createButton("EXIT");
        Button btnTable = createButton("BẢNG XẾP HẠNG");
        Button btnMultiplayer = createButton("MULTIPLAYER");

        container.getChildren().addAll(titleText, btnStart, btnSettings, btnExit, btnTable, btnMultiplayer);
        getChildren().add(container);
        btnMultiplayer.setOnAction(e -> openMultiplayerMenu(container));
        btnStart.setOnAction(e -> {
            BoxBlur blur = new BoxBlur(10, 10, 3);
            container.setEffect(blur);

            RectanglePane overlay = new RectanglePane();
            overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

            VBox nameBox = new VBox(15);
            nameBox.setAlignment(Pos.CENTER);
            nameBox.setBackground(new Background(new BackgroundFill(
                    Color.web("#220000"),
                    new CornerRadii(15),
                    Insets.EMPTY
            )));
            nameBox.setBorder(new Border(new BorderStroke(
                    Color.web("#ff6600"),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(15),
                    new BorderWidths(2)
            )));
            nameBox.setPadding(new Insets(30));
            nameBox.setMaxWidth(400);

            Label label = new Label("Enter your username:");
            label.setTextFill(Color.web("#ffcc66"));
            label.setFont(Font.font("Consolas", 18));

            TextField nameField = new TextField();
            nameField.setPromptText("Your name...");
            nameField.setMaxWidth(250);
            overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            nameField.setStyle("-fx-background-color: #330000; -fx-text-fill: white; -fx-border-color: orange;");

            HBox btnBox = new HBox(15);
            btnBox.setAlignment(Pos.CENTER);

            Button btnPlay = createButton("PLAY");
            Button btnBack = createButton("BACK");

            btnPlay.setOnAction(ev -> {
                String username = nameField.getText().trim();
                if (!username.isEmpty()) {
                    Config.PLAYER_NAME = username;
                    if (onStart != null) onStart.run();
                } else {
                    nameField.setPromptText("⚠ Please enter your name!");
                    nameField.setStyle("-fx-background-color: #330000; -fx-border-color: red;");
                }
            });

            btnBack.setOnAction(ev -> {
                getChildren().removeAll(overlay, nameBox);
                container.setEffect(null);
            });

            btnBox.getChildren().addAll(btnPlay, btnBack);
            nameBox.getChildren().addAll(label, nameField, btnBox);

            StackPane.setAlignment(nameBox, Pos.CENTER);

            getChildren().addAll(overlay, nameBox);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), nameBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        btnTable.setOnAction(e -> {
            BoxBlur blur = new BoxBlur(10, 10, 3);
            container.setEffect(blur);

            RectanglePane overlay = new RectanglePane();
            overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

            VBox tableBox = new VBox(15);
            tableBox.setAlignment(Pos.CENTER);
            tableBox.setPadding(new Insets(30));
            tableBox.setBackground(new Background(new BackgroundFill(
                    Color.web("#220000"),
                    new CornerRadii(15),
                    Insets.EMPTY
            )));
            tableBox.setBorder(new Border(new BorderStroke(
                    Color.web("#ff6600"),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(15),
                    new BorderWidths(2)
            )));
            tableBox.setMaxWidth(500);

            Label rankingTitle = new Label("🏆 BẢNG XẾP HẠNG 🏆");
            rankingTitle.setTextFill(Color.web("#ffcc66"));
            rankingTitle.setFont(Font.font("Consolas", 22));

            VBox list = new VBox(10);
            list.setAlignment(Pos.CENTER);
            try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/scores.txt"))) {
                String line;
                int rank = 1;
                while ((line = reader.readLine()) != null && rank <= 10) {
                    Label row = new Label(rank + ". " + line);
                    row.setTextFill(Color.web("#ffcc99"));
                    row.setFont(Font.font("Consolas", 16));
                    list.getChildren().add(row);
                    rank++;
                }

                if (rank == 1) {
                    Label empty = new Label("Chưa có người chơi nào!");
                    empty.setTextFill(Color.LIGHTGRAY);
                    list.getChildren().add(empty);
                }
            } catch (IOException ex) {
                Label empty = new Label("Chưa có file điểm nào!");
                empty.setTextFill(Color.LIGHTGRAY);
                list.getChildren().add(empty);
            }

            Button btnBack = createButton("BACK");
            btnBack.setOnAction(ev -> {
                getChildren().removeAll(overlay, tableBox);
                container.setEffect(null);
            });

            tableBox.getChildren().addAll(rankingTitle, list, btnBack);
            StackPane rankingPane = new StackPane(overlay, tableBox);
            rankingPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            rankingPane.setAlignment(Pos.CENTER);
            getChildren().add(rankingPane);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), tableBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }
    private void openMultiplayerMenu(VBox container) {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        container.setEffect(blur);

        RectanglePane overlay = new RectanglePane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        VBox mpBox = new VBox(15);
        mpBox.setAlignment(Pos.CENTER);
        mpBox.setPadding(new Insets(30));
        mpBox.setBackground(new Background(new BackgroundFill(
                Color.web("#220000"), new CornerRadii(15), Insets.EMPTY)));
        mpBox.setBorder(new Border(new BorderStroke(
                Color.web("#ff6600"), BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(2))));
        mpBox.setMaxWidth(500);

        Label lbl = new Label("🎮 MULTIPLAYER MODE 🎮");
        lbl.setTextFill(Color.web("#ffcc66"));
        lbl.setFont(Font.font("Consolas", 22));

        Button btnHost = createButton("TẠO PHÒNG (HOST)");
        Button btnJoin = createButton("THAM GIA PHÒNG (CLIENT)");
        Button btnBack = createButton("BACK");

        Label status = new Label();
        status.setTextFill(Color.web("#ffcc99"));
        status.setFont(Font.font("Consolas", 16));

        btnHost.setOnAction(ev -> startAsHost(status));
        btnJoin.setOnAction(ev -> openJoinDialog(status));
        btnBack.setOnAction(ev -> {
            getChildren().removeAll(overlay, mpBox);
            container.setEffect(null);
        });

        mpBox.getChildren().addAll(lbl, btnHost, btnJoin, status, btnBack);
        getChildren().addAll(overlay, mpBox);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), mpBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    private void startAsHost(Label status) {
        org.example.baitaplamgame.Network.Server server = new org.example.baitaplamgame.Network.Server();
        server.setOnMessageListener(msg -> javafx.application.Platform.runLater(() -> status.setText(msg)));
        status.setText("🕓 Đang mở phòng tại cổng 5000...");
        server.startServer(5000);
    }
    private void openJoinDialog(Label status) {
        VBox ipBox = new VBox(10);
        ipBox.setAlignment(Pos.CENTER);
        ipBox.setPadding(new Insets(20));
        ipBox.setBackground(new Background(new BackgroundFill(
                Color.web("#330000"), new CornerRadii(10), Insets.EMPTY)));

        Label lblIp = new Label("Nhập IP của Host:");
        lblIp.setTextFill(Color.web("#ffcc66"));

        TextField txtIp = new TextField();
        txtIp.setPromptText("VD: 192.168.1.10");
        txtIp.setMaxWidth(200);
        Button btnConnect = createButton("KẾT NỐI");
        ipBox.getChildren().addAll(lblIp, txtIp, btnConnect);
        StackPane.setAlignment(ipBox, Pos.CENTER);
        getChildren().add(ipBox);

        btnConnect.setOnAction(ev -> {
            String ip = txtIp.getText().trim();
            if (!ip.isEmpty()) {
                org.example.baitaplamgame.Network.Client client = new org.example.baitaplamgame.Network.Client();
                client.setOnMessageListener(msg -> javafx.application.Platform.runLater(() -> status.setText(msg)));
                client.connect(ip, 5000);
                getChildren().remove(ipBox);
            } else {
                txtIp.setPromptText("Vui lòng nhập IP!");
            }
        });
    }




    private Button createButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Consolas", 18));
        button.setTextFill(Color.web("#ffcc66"));
        button.setPrefWidth(200);
        button.setBackground(new Background(new BackgroundFill(
                Color.web("#330000"),
                new CornerRadii(10),
                Insets.EMPTY
        )));
        button.setBorder(new Border(new BorderStroke(
                Color.web("#ff6600"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(2)
        )));

        button.setOnMouseEntered(e -> {
            button.setTextFill(Color.WHITE);
            button.setEffect(new DropShadow(15, Color.ORANGE));
        });
        button.setOnMouseExited(e -> {
            button.setTextFill(Color.web("#ffcc66"));
            button.setEffect(null);
        });

        return button;
    }

    private static class RectanglePane extends Pane {}
}
