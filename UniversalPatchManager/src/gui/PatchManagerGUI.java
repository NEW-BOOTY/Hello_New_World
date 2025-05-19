/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PatchManagerGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Universal Patch Manager");

        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        Label title = new Label("Universal Patch Manager");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button applyPatchButton = new Button("Apply Patch");
        Button rollbackButton = new Button("Rollback Update");
        Button viewLogsButton = new Button("View Logs");

        layout.getChildren().addAll(title, applyPatchButton, rollbackButton, viewLogsButton);

        applyPatchButton.setOnAction(e -> System.out.println("Applying Patch..."));
        rollbackButton.setOnAction(e -> System.out.println("Rolling Back Update..."));
        viewLogsButton.setOnAction(e -> System.out.println("Viewing Logs..."));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
