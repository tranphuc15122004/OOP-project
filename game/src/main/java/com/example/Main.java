package com.example;

import com.example.Game.GamePvP;
import com.example.Game.Mouse;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    Mouse mouse = new Mouse();
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        StackPane root= new StackPane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT );
        root.getChildren().addAll(canvas);
        root.setStyle("-fx-background-color: black;");

        GraphicsContext gc = canvas.getGraphicsContext2D();
        GamePvP game  = new GamePvP(gc, mouse , canvas);
        game.gameloop();

        canvas.setOnMousePressed(e -> {
            mouse.setPressed(true);
        });
        canvas.setOnMouseMoved(e -> {
            mouse.x = (int) e.getX();
            mouse.y = (int) e.getY();
        });
        canvas.setOnMouseDragged(e -> {
            mouse.x = (int) e.getX();
            mouse.y = (int) e.getY();
        });
        canvas.setOnMouseReleased(e -> {
            mouse.setPressed(false);
        });

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}