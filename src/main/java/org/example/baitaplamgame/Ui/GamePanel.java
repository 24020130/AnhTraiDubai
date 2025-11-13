package org.example.baitaplamgame.Ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import org.example.baitaplamgame.Model.GameManager;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ScoreFileManager;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GamePanel extends Pane {
    private Runnable onStart;
    private int currentUnlockedLevel = 1;
    private int currentSkinIndex = 0;
    private static final String[] SKINS = {"default.png", "blue.png", "green.png"};

    public void setOnStart(Runnable onStart) {
        this.onStart = onStart;
    }

    public GamePanel() {
        // Đọc tiến độ đã lưu (nếu có)
        File progressFile = new File("progress.txt");
        if (progressFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(progressFile))) {
                String line = br.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    currentUnlockedLevel = Integer.parseInt(line.trim());
                } else {
                    // nếu file rỗng hoặc không hợp lệ, đặt về level 1
                    System.out.println("⚠ progress.txt rỗng, bắt đầu từ Level 1.");
                    currentUnlockedLevel = 1;
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("⚠ Lỗi đọc progress.txt, bắt đầu từ Level 1.");
                currentUnlockedLevel = 1;
            }
        } else {
            System.out.println("⚠ Chưa có file progress, bắt đầu từ Level 1.");
            currentUnlockedLevel = 1;
        }


        // Nền chính
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
        Button btnTable = createButton("BẢNG XẾP HẠNG");
        Button btnMultiplayer = createButton("MULTIPLAYER");
        Button btnGuide = createButton("HƯỚNG DẪN");
        Button btnPLayer = createButton("Player");

        container.getChildren().addAll(titleText, btnStart, btnSettings, btnTable, btnMultiplayer, btnPLayer, btnGuide);
        getChildren().add(container);
        btnMultiplayer.setOnAction(e -> openMultiplayerMenu(container));
        btnStart.setOnAction(e -> {
            // 🔥 Cần khai báo các biến trước khi sử dụng chúng để tạo StackPane
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

            // 🔥 Căn giữa màn hình Đăng nhập (Username)
            final StackPane centeredNamePane = new StackPane(overlay, nameBox);
            centeredNamePane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

            // FIX: Thay đổi logic xóa để sử dụng centeredNamePane
            btnPlay.setOnAction(ev -> {
                String username = nameField.getText().trim();
                if (!username.isEmpty()) {
                    Config.PLAYER_NAME = username;
                    getChildren().remove(centeredNamePane); // Xóa StackPane
                    container.setEffect(null);
                    showLevelSelection(); // Gọi màn hình chọn level
                } else {
                    nameField.setPromptText("⚠ Please enter your name!");
                    nameField.setStyle("-fx-background-color: #330000; -fx-border-color: red;");
                }
            });


            // FIX: Thay đổi logic xóa để sử dụng centeredNamePane
            btnBack.setOnAction(ev -> {
                getChildren().remove(centeredNamePane); // Xóa StackPane
                container.setEffect(null);
            });

            btnBox.getChildren().addAll(btnPlay, btnBack);
            nameBox.getChildren().addAll(label, nameField, btnBox);

            // Thêm StackPane đã căn chỉnh vào GamePanel
            getChildren().add(centeredNamePane);

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
            btnBack.setOnAction(ev -> {
                // ✅ FIX: Xóa rankingPane ra khỏi GamePanel
                getChildren().remove(rankingPane);
                container.setEffect(null);
            });
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), tableBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        btnGuide.setOnAction(e -> showGuide());
        btnSettings.setOnAction(e -> openSettingsMenu(container));
        btnPLayer.setOnAction(e -> openSkinSelectionMenu(container));
    }
    private void showGuide() {
        // Ảnh hướng dẫn
        Image image = new Image(getClass().getResource("/images/huongdan.png").toExternalForm());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(Config.WINDOW_WIDTH);
        imageView.setFitHeight(Config.WINDOW_HEIGHT);

        // Lớp phủ (full màn hình)
        AnchorPane overlay = new AnchorPane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.getChildren().add(imageView);

        // Nút back
        Button backButton = createButton("BACK");
        backButton.setLayoutX(50);
        backButton.setLayoutY(50);
        overlay.getChildren().add(backButton);

        // Khi nhấn back → quay lại menu
        backButton.setOnAction(e -> getChildren().remove(overlay));

        // Thêm overlay lên giao diện
        getChildren().add(overlay);
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

        // 🔥 Tạo StackPane để căn giữa mpBox
        final StackPane centeredMPPane = new StackPane(overlay, mpBox);
        centeredMPPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        btnHost.setOnAction(ev -> startAsHost(status));
        btnJoin.setOnAction(ev -> openJoinDialog(status));
        btnBack.setOnAction(ev -> {
            // FIX: Xóa StackPane chứa mpBox
            getChildren().remove(centeredMPPane);
            container.setEffect(null);
        });

        mpBox.getChildren().addAll(lbl, btnHost, btnJoin, status, btnBack);

        // 🔥 Thêm StackPane đã căn giữa vào GamePanel
        getChildren().add(centeredMPPane);

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


    private void showLevelSelection() {
        // Nền neon đỏ
        getChildren().clear();
        Pane levelPane = new Pane();
        levelPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        levelPane.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, null,
                        new Stop[]{
                                new Stop(0, Color.web("#1a0000")),
                                new Stop(1, Color.web("#400000"))
                        }),
                CornerRadii.EMPTY, Insets.EMPTY
        )));

        // Tiêu đề
        Text title = new Text("SELECT LEVEL");
        title.setFont(Font.font("Consolas", 40));
        title.setFill(Color.web("#ff3333"));
        title.setEffect(new DropShadow(30, Color.RED));
        title.setLayoutY(100);
        title.setLayoutX(Config.WINDOW_WIDTH / 2 - 150);

        // Lưới 2 hàng 3 cột
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(40);
        grid.setAlignment(Pos.CENTER);
        grid.setLayoutY(Config.WINDOW_HEIGHT / 2 - 100);
        grid.setLayoutX(Config.WINDOW_WIDTH / 2 - 300);

        for (int i = 1; i <= 6; i++) {
            Button levelBtn = createLevelButton("LEVEL " + i, i);
            grid.add(levelBtn, (i - 1) % 3, (i - 1) / 3);
        }

        // Nút back
        Button backBtn = new Button("BACK");
        backBtn.setFont(Font.font("Consolas", 20));
        backBtn.setTextFill(Color.web("#ffcccc"));
        backBtn.setBackground(new Background(new BackgroundFill(Color.web("#330000"), new CornerRadii(10), Insets.EMPTY)));
        backBtn.setBorder(new Border(new BorderStroke(Color.web("#ff3333"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        backBtn.setLayoutX(50);
        backBtn.setLayoutY(50);
        backBtn.setOnMouseEntered(e -> backBtn.setEffect(new DropShadow(20, Color.RED)));
        backBtn.setOnMouseExited(e -> backBtn.setEffect(null));

        backBtn.setOnAction(e -> {
            getChildren().remove(levelPane);
            GamePanel mainMenu = new GamePanel();
            Scene scene = getScene();
            if (scene != null) {
                scene.setRoot(mainMenu);
            } else {
                getChildren().add(mainMenu);
            }
        });

        levelPane.getChildren().addAll(title, grid, backBtn);
        getChildren().add(levelPane);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), levelPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private Button createLevelButton(String text, int level) {
        Button btn = new Button(text);
        btn.setPrefSize(180, 100);
        btn.setFont(Font.font("Consolas", 20));

        boolean unlocked = level <= currentUnlockedLevel;

        if (unlocked) {
            // Nút mở khóa (đỏ sáng, có hiệu ứng)
            btn.setTextFill(Color.web("#ff6666"));
            btn.setBackground(new Background(new BackgroundFill(Color.web("#220000"), new CornerRadii(15), Insets.EMPTY)));
            btn.setBorder(new Border(new BorderStroke(Color.web("#ff0000"), BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(3))));
            btn.setEffect(new DropShadow(15, Color.RED));

            btn.setOnMouseEntered(e -> {
                btn.setTextFill(Color.WHITE);
                btn.setEffect(new DropShadow(30, Color.ORANGERED));
                btn.setScaleX(1.1);
                btn.setScaleY(1.1);
            });
            btn.setOnMouseExited(e -> {
                btn.setTextFill(Color.web("#ff6666"));
                btn.setEffect(new DropShadow(15, Color.RED));
                btn.setScaleX(1.0);
                btn.setScaleY(1.0);
            });

            btn.setOnAction(e -> startLevel(level));

        } else {
            // Level bị khóa (tối màu, không click)
            btn.setTextFill(Color.web("#555555"));
            btn.setBackground(new Background(new BackgroundFill(Color.web("#110000"), new CornerRadii(15), Insets.EMPTY)));
            btn.setBorder(new Border(new BorderStroke(Color.web("#660000"), BorderStrokeStyle.DASHED, new CornerRadii(15), new BorderWidths(3))));
            btn.setEffect(new DropShadow(10, Color.web("#330000")));

            btn.setOnAction(e -> {
                Label lockedLabel = new Label("🔒 Level " + level + " chưa được mở!");
                lockedLabel.setTextFill(Color.web("#ffaaaa"));
                lockedLabel.setFont(Font.font("Consolas", 18));
                lockedLabel.setBackground(new Background(new BackgroundFill(Color.web("#330000"), new CornerRadii(10), Insets.EMPTY)));
                lockedLabel.setPadding(new Insets(10));
                StackPane.setAlignment(lockedLabel, Pos.TOP_CENTER);
                getChildren().add(lockedLabel);

                FadeTransition fadeMsg = new FadeTransition(Duration.seconds(2), lockedLabel);
                fadeMsg.setFromValue(1);
                fadeMsg.setToValue(0);
                fadeMsg.setOnFinished(ev -> getChildren().remove(lockedLabel));
                fadeMsg.play();
            });
        }

        return btn;
    }

    private void startLevel(int level) {
        System.out.println("Starting Level " + level + " for player: " + Config.PLAYER_NAME);

        // Xóa mọi thứ cũ (menu / overlay / levelPane)
        getChildren().clear();

        // Tạo Pane cho gameplay
        Pane gameplayPane = new Pane();
        gameplayPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        gameplayPane.setStyle("-fx-background-color: black;");

        // Nút quay lại menu nhanh
        Button quickExit = new Button("↩ Menu");
        quickExit.setFont(Font.font("Consolas", 16));
        quickExit.setLayoutX(20);
        quickExit.setLayoutY(20);
        quickExit.setOnAction(e -> {
            getChildren().remove(gameplayPane);
            showLevelSelection();
        });
        gameplayPane.getChildren().add(quickExit);

        // Thêm gameplayPane vào
        getChildren().add(gameplayPane);

        // Tạo GameManager
        GameManager gameManager = new GameManager(gameplayPane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // Callback khi thoát về menu
        gameManager.setOnExitToMenu(() -> Platform.runLater(() -> {
            getChildren().remove(gameplayPane);
            showLevelSelection();
        }));

        // Callback khi hoàn thành level
        // Logic mới trong GamePanel
        gameManager.setOnLevelComplete(completedLevel -> Platform.runLater(() -> {
            System.out.println("✅ Level " + completedLevel + " hoàn thành! Đang quay lại menu chọn level.");
            gameManager.stopGame();
            unlockNextLevel(completedLevel);
            getChildren().remove(gameplayPane);
            showLevelSelection();
        }));


        // Thiết lập input và bắt đầu game: Đảm bảo start chỉ xảy ra 1 lần.
        // Sử dụng PauseTransition ngắn để đợi Scene sẵn sàng và loại bỏ lỗi khởi động kép.
        javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(50));
        pt.setOnFinished(e -> {
            Scene scene = getScene();
            if (scene != null) {
                gameManager.setupInput(scene);
                this.setFocusTraversable(true);
                this.requestFocus();

                // 🔥 CHỈ GỌI START Ở ĐÂY
                gameManager.startLevelNumber(level);
            } else {
                System.err.println("⚠️ Không thể bắt đầu game vì Scene vẫn là null.");
            }
        });
        pt.play();

        // Callback khi GAME OVER
        // ... (Tiếp tục với khối onGameOver và FadeTransition)

        // Callback khi GAME OVER
        // Callback khi GAME OVER
        gameManager.setOnGameOver(() -> Platform.runLater(() -> {
            System.out.println("💀 Game Over — quay lại menu chính");
            getChildren().clear();

            // 🔥 Gọi lại màn hình menu chính (nơi có START, SETTINGS, v.v)
            GamePanel newMenu = new GamePanel();
            Scene scene = getScene();
            if (scene != null) {
                scene.setRoot(newMenu);
            } else {
                getChildren().add(newMenu);
            }
        }));


        // Hiệu ứng fade-in
        FadeTransition fade = new FadeTransition(Duration.seconds(0.6), gameplayPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }


    private void unlockNextLevel(int completedLevel) {
        // Nếu hoàn thành level nhỏ hơn level tối đa thì mở khóa tiếp theo
        if (completedLevel >= currentUnlockedLevel && currentUnlockedLevel < 6) {
            currentUnlockedLevel = completedLevel + 1;
            System.out.println("🎉 Mở khóa Level " + currentUnlockedLevel);

            // Lưu tiến độ vào file
            try (java.io.PrintWriter writer = new java.io.PrintWriter("progress.txt")) {
                writer.println(currentUnlockedLevel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void openSettingsMenu(VBox container) {
        // Tương tự như các overlay khác, làm mờ menu chính
        BoxBlur blur = new BoxBlur(10, 10, 3);
        container.setEffect(blur);

        RectanglePane overlay = new RectanglePane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Đen đậm hơn

        VBox settingsBox = new VBox(20);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setPadding(new Insets(40));
        settingsBox.setBackground(new Background(new BackgroundFill(
                Color.web("#220000"),
                new CornerRadii(15),
                Insets.EMPTY
        )));
        settingsBox.setBorder(new Border(new BorderStroke(
                Color.web("#ff6600"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(15),
                new BorderWidths(2)
        )));
        settingsBox.setMaxWidth(450);

        Label title = new Label("⚙️ CÀI ĐẶT ⚙️");
        title.setTextFill(Color.web("#ffcc66"));
        title.setFont(Font.font("Consolas", 24));

        // --- Kiểm soát Âm lượng ---

        Label volumeLabel = new Label("Âm lượng Chính (Master Volume):");
        volumeLabel.setTextFill(Color.web("#ffcc99"));
        volumeLabel.setFont(Font.font("Consolas", 16));

        // Slider để điều chỉnh âm lượng (giá trị từ 0.0 đến 1.0)
        javafx.scene.control.Slider volumeSlider = new javafx.scene.control.Slider(0, 1.0, 0.5);

        // 🔥 FIX 1: Thiết lập giá trị ban đầu theo SoundManager
        volumeSlider.setValue(org.example.baitaplamgame.Utlis.SoundManager.getVolume());

        volumeSlider.setPrefWidth(300);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.5);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setStyle("-fx-control-inner-background: #330000; -fx-text-fill: white;");

        // 🔥 FIX 2: Thêm Listener để kết nối Slider với SoundManager
        volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            double vol = newValue.doubleValue();
            // Kết nối chính xác với phương thức setVolume()
            org.example.baitaplamgame.Utlis.SoundManager.setVolume(vol);
            System.out.println("Volume set to: " + vol);
        });

        // --- Nút Back ---

        // Tạo StackPane trước để biến centeredPane thành final
        final StackPane centeredPane = new StackPane(overlay, settingsBox);
        centeredPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        Button btnBack = createButton("BACK");
        btnBack.setOnAction(ev -> {
            // FIX: Xóa container cha (centeredPane) khỏi GamePanel
            getChildren().remove(centeredPane);
            container.setEffect(null); // Bỏ hiệu ứng làm mờ trên Menu chính
        });

        settingsBox.getChildren().addAll(title, volumeLabel, volumeSlider, btnBack);


        // Thêm StackPane đã căn chỉnh vào GamePanel
        getChildren().add(centeredPane);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), settingsBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    // Thêm vào lớp GamePanel

    private void openSkinSelectionMenu(VBox container) {
        // 1. Setup Overlay
        BoxBlur blur = new BoxBlur(10, 10, 3);
        container.setEffect(blur);

        RectanglePane overlay = new RectanglePane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");

        VBox skinBox = new VBox(25);
        skinBox.setAlignment(Pos.CENTER);
        skinBox.setPadding(new Insets(30));
        skinBox.setBackground(new Background(new BackgroundFill(
                Color.web("#180000"),
                new CornerRadii(15),
                Insets.EMPTY
        )));
        skinBox.setBorder(new Border(new BorderStroke(
                Color.web("#ff6600"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(15),
                new BorderWidths(2)
        )));
        skinBox.setMaxWidth(400);

        Label title = new Label("🎮 CHỌN SKIN (PLAYER) 🎨");
        title.setTextFill(Color.web("#ffcc66"));
        title.setFont(Font.font("Consolas", 24));

        // 2. Ảnh minh họa
        ImageView skinView = new ImageView();
        skinView.setFitWidth(100);
        skinView.setFitHeight(100);
        skinView.setPreserveRatio(true);
        skinView.setEffect(new DropShadow(20, Color.WHITE));

        Label skinNameLabel = new Label();
        skinNameLabel.setTextFill(Color.web("#ff9999"));
        skinNameLabel.setFont(Font.font("Consolas", 18));

        // Danh sách các tên file skin
        // Sử dụng SKINS đã được khai báo là biến thành viên (instance variable)
        // final String[] SKINS = {"default.png", "blue.png", "green.png"};

        // Khởi tạo currentSkinIndex dựa trên skin đang được lưu trong Config
        for (int i = 0; i < SKINS.length; i++) {
            if (SKINS[i].equals(Config.CURRENT_PLAYER_SKIN)) {
                currentSkinIndex = i;
                break;
            }
        }


        // 3. Logic hiển thị và chuyển đổi
        Runnable updateSkinDisplay = () -> {
            String skinFileName = SKINS[currentSkinIndex];

            // LƯU VÀO CONFIG ĐỂ CẬP NHẬT TRONG TRẬN ĐẤU
            Config.CURRENT_PLAYER_SKIN = skinFileName;

            // Load ảnh skin tương ứng
            var resource = getClass().getResourceAsStream("/skins/" + skinFileName);
            if (resource != null) {
                Image newSkinImage = new Image(resource);
                skinView.setImage(newSkinImage);
            } else {
                System.err.println("Không tìm thấy skin: /skins/" + skinFileName);
            }

            // Cập nhật tên skin
            String name = skinFileName.substring(0, skinFileName.lastIndexOf('.')).toUpperCase();
            skinNameLabel.setText("Skin: " + name);
        };

        Button btnNext = createButton(">");
        Button btnPrev = createButton("<");
        btnNext.setPrefWidth(50);
        btnPrev.setPrefWidth(50);

        // Chuyển đổi Skin
        btnNext.setOnAction(e -> {
            currentSkinIndex = (currentSkinIndex + 1) % SKINS.length;
            updateSkinDisplay.run();
        });

        btnPrev.setOnAction(e -> {
            currentSkinIndex = (currentSkinIndex - 1 + SKINS.length) % SKINS.length;
            updateSkinDisplay.run();
        });

        // 4. Căn giữa và Hiển thị Toàn màn hình (TẠO TRƯỚC ĐỂ SỬ DỤNG TRONG btnSelect)
        final StackPane centeredSkinPane = new StackPane(overlay, skinBox);
        centeredSkinPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);


        Button btnSelect = createButton("CHỌN VÀ QUAY LẠI");
        btnSelect.setOnAction(e -> {
            // FIX: Bây giờ centeredSkinPane đã được khai báo và có thể truy cập
            getChildren().remove(centeredSkinPane); // Xóa StackPane
            container.setEffect(null);
        });

        // 5. Xây dựng Layout
        HBox navBox = new HBox(15, btnPrev, skinView, btnNext);
        navBox.setAlignment(Pos.CENTER);

        skinBox.getChildren().addAll(title, skinNameLabel, navBox, btnSelect);

        // 6. Thêm StackPane đã căn chỉnh vào GamePanel
        getChildren().add(centeredSkinPane);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), skinBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Khởi tạo hiển thị lần đầu tiên
        updateSkinDisplay.run();
    }
    private static class RectanglePane extends Pane {}
}