package org.example.baitaplamgame.Utlis;

import java.util.HashSet;
import java.util.Set;

public class InputKeys {
    private static final Set<String> pressedKeys = new HashSet<>();

    public static void setKeyPressed(String key) {
        pressedKeys.add(key);
    }

    public static void setKeyReleased(String key) {
        pressedKeys.remove(key);
    }

    public static boolean isPressed(String key) {
        return pressedKeys.contains(key);
    }
}

