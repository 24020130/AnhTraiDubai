package org.example.baitaplamgame.Network;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.baitaplamgame.Model.GameManager;
import org.example.baitaplamgame.Utlis.Config;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter printWriter;
    private OnMessageListener listener;
    private GameManager gameManager;
    private Stage gameStage;
    private Runnable onGameEndToMenu;
    private Runnable onGameStart;
    private boolean gameEndSent = false;

    public interface OnMessageListener {
        void onMessage(String msg);
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    public void setOnGameEndToMenu(Runnable callback) {
        this.onGameEndToMenu = callback;
    }

    public void setOnGameStart(Runnable callback) {
        this.onGameStart = callback;
    }

    public void connect(String serverIp, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(serverIp, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                sendMessageToUI("✅ Đã kết nối đến server!");

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Server: " + line);
                    sendMessageToUI("Server: " + line);

                    if (line.equals("START_GAME")) {
                        startGameUI();
                    } else if (line.startsWith("OPPONENT_POS:")) {
                        double y = Double.parseDouble(line.split(":")[1]);
                        Platform.runLater(() -> {
                            if (gameManager != null && gameManager.getPaddle() != null)
                                gameManager.getPaddle().setY(y);
                        });
                    } else if (line.startsWith("GAME_OVER:")) {
                        String result = line.split(":")[1];
                        Platform.runLater(() -> handleGameEnd(result));
                    }
                }

            } catch (IOException e) {
                sendMessageToUI("❌ Không thể kết nối đến server!");
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
    }

    private void startGameUI() {
        Platform.runLater(() -> {
            try {
                Pane pane = new Pane();
                gameManager = new GameManager(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                gameEndSent = false;

                if (onGameEndToMenu != null)
                    gameManager.setOnGameEndToMenu(() -> Platform.runLater(() -> {
                        if (gameStage != null && gameStage.isShowing()) gameStage.close();
                        onGameEndToMenu.run();
                    }));

                gameStage = new Stage();
                gameStage.setTitle("🎮 Client - Multiplayer Game");
                Scene scene = new Scene(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                gameStage.setScene(scene);
                gameManager.setupInput(scene);
                scene.getRoot().requestFocus();

                gameManager.setOnPaddleMove(y -> send("MOVE:" + y));

                gameManager.setOnGameEnd((isClientWin) -> {
                    if (gameEndSent) return;
                    gameEndSent = true;

                    if (isClientWin) {
                        send("GAME_OVER:LOSE");
                        showResultAndReturnMenu("WIN");
                    } else {
                        send("GAME_OVER:WIN");
                        showResultAndReturnMenu("LOSE");
                    }
                });

                gameStage.setOnCloseRequest(event -> closeConnection());
                gameStage.show();

                if (onGameStart != null) onGameStart.run();

                gameManager.startLevelNumber(6);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleGameEnd(String result) {
        if (!gameEndSent) {
            gameEndSent = true;
            showResultAndReturnMenu(result);
        }
    }

    private void showResultAndReturnMenu(String result) {
        if (gameManager != null) {
            gameManager.showGameResult(result);
        }

        // Delay 2 giây để người chơi xem kết quả
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(ev -> {
            // Đóng Stage game
            if (gameStage != null && gameStage.isShowing()) {
                gameStage.close();
            }
            // Gọi menu
            if (onGameEndToMenu != null) onGameEndToMenu.run();
        });
        pause.play();
    }

    public void send(String msg) {
        if (printWriter != null) printWriter.println(msg);
    }

    private void sendMessageToUI(String message) {
        if (listener != null) Platform.runLater(() -> listener.onMessage(message));
    }

    public void closeConnection() {
        try {
            if (reader != null) reader.close();
            if (printWriter != null) printWriter.close();
            if (socket != null && !socket.isClosed()) socket.close();

            Platform.runLater(() -> {
                if (gameStage != null && gameStage.isShowing()) gameStage.close();
            });

            sendMessageToUI("🔌 Đã ngắt kết nối khỏi server!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
