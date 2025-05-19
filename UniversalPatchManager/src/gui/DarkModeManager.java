/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */
package gui;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DarkModeManager {

    public void enableDarkMode(Stage stage) {
        Scene scene = stage.getScene();
        scene.setFill(Color.DARKGRAY);
    }

    public void disableDarkMode(Stage stage) {
        Scene scene = stage.getScene();
        scene.setFill(Color.WHITE);
    }
}
