package org.example.baitaplamgame.Network;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.baitaplamgame.Model.GameManager;
import org.example.baitaplamgame.Utlis.Config;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private OnMessageListener listener;
    private GameManager gameManager;

    public interface OnMessageListener {
        void onMessage(String msg);
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    /**
     * Kết nối đến server
     */
    public void connect(String serverIp, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(serverIp, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                sendMessageToUI("✅ Đã kết nối đến server!");

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Server: " + line);
                    sendMessageToUI("Server: " + line);

                    switch (line) {
                        case "START_GAME":
                            startGameUI();
                            break;

                        case "PLAYER_DEAD":
                            Platform.runLater(() -> {
                                if (gameManager != null) gameManager.showGameOver("Bạn thua!");
                            });
                            break;

                        case "ENEMY_DEAD":
                        case "PLAYER_SCORE_WIN":
                            Platform.runLater(() -> {
                                if (gameManager != null) gameManager.showWinnerEffect();
                            });
                            break;

                        default:
                            // Có thể thêm xử lý message khác ở đây
                            break;
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

    /**
     * Hàm gọi giao diện khởi động game
     */
    private void startGameUI() {
        Platform.runLater(() -> {
            try {
                Pane pane = new Pane();
                gameManager = new GameManager(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                gameManager.setWriter(writer);
                gameManager.startGame();

                Stage stage = new Stage();
                stage.setTitle("🎮 Client - Multiplayer Game");

                Scene scene = new Scene(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                stage.setScene(scene);
                gameManager.setupInput(scene);

                stage.setOnCloseRequest(event -> closeConnection());
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Gửi tin nhắn đến server
     */
    public void send(String msg) {
        new Thread(() -> {
            try {
                if (writer != null) {
                    writer.write(msg + "\n");
                    writer.flush();
                }
            } catch (IOException e) {
                sendMessageToUI("⚠️ Lỗi khi gửi tin nhắn!");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Gửi thông điệp ra UI thread
     */
    private void sendMessageToUI(String message) {
        if (listener != null) {
            Platform.runLater(() -> listener.onMessage(message));
        }
    }

    /**
     * Đóng kết nối an toàn
     */
    public void closeConnection() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
            sendMessageToUI("🔌 Đã ngắt kết nối khỏi server!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
