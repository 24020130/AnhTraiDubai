package org.example.baitaplamgame.Utlis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreFileManager {
    private static final String FILE_PATH = "scores.txt";

    public static void saveScore(String playerName, int score, int level) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);

        String line = String.format("%s | %d | Level %d | %s", playerName, score, level, timestamp);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(line);
            writer.newLine();
            System.out.println("Đã lưu điểm vào file: " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu điểm: " + e.getMessage());
        }
    }
    public static int getUnlockedLevel() {
        // Nếu bạn chưa làm lưu file thì tạm trả về 1 (chỉ mở Level 1)
        return 1;
    }

    public static void unlockNextLevel(int currentLevel) {
        // Tạm chưa lưu ra file, chỉ để tránh lỗi
        System.out.println("Unlocked level " + (currentLevel + 1));
    }

}
