package org.example.baitaplamgame.Utlis;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static MediaPlayer backgroundPlayer;
    private static double volume = 0.5; // âm lượng chung (0.0 - 1.0)
    private static Map<String, AudioClip> effectMap = new HashMap<>();

    // 🔹 Phát nhạc nền (loop vô hạn)
    public static void playBackground(String fileName) {
        stopBackground();

        var resource = SoundManager.class.getResource("/sounds/" + fileName);
        if (resource == null) {
            System.err.println("Không tìm thấy file nhạc: " + fileName);
            return;
        }

        Media media = new Media(resource.toString());
        backgroundPlayer = new MediaPlayer(media);
        backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundPlayer.setVolume(volume);
        backgroundPlayer.play();
    }

    // 🔹 Dừng nhạc nền
    public static void stopBackground() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
            backgroundPlayer = null;
        }
    }
    public static void stopAllEffects() {
        for (AudioClip clip : effectMap.values()) {
            if (clip.isPlaying()) {
                clip.stop();
            }
        }
    }

    // 🔹 Phát hiệu ứng âm thanh
    public static void playEffect(String fileName) {
        AudioClip clip = effectMap.get(fileName);

        if (clip == null) {
            var resource = SoundManager.class.getResource("/sounds/" + fileName);
            if (resource == null) {
                System.err.println("Không tìm thấy hiệu ứng: " + fileName);
                return;
            }
            clip = new AudioClip(resource.toString());
            clip.setVolume(volume);
            effectMap.put(fileName, clip);
        }

        clip.play();
    }
    public static void setVolume(double vol) {
        volume = Math.max(0, Math.min(1, vol));

        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(volume);
        }

        for (AudioClip clip : effectMap.values()) {
            clip.setVolume(volume);
        }
    }
    public static double getVolume() {
        return volume;
    }
}
