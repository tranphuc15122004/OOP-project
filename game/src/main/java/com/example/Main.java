package com.example;

import com.example.Game.GamePvB;
import com.example.Game.GamePvP;
import com.example.Game.Mouse;
import com.example.Game.Puzzle;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static Scene scene;
    private static Stage primaryStage;
    private static String gameMode ="";
    private static String bool ="";
    Mouse mouse = new Mouse();
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        intMainmenu(primaryStage);
    }

    private void intMainmenu(Stage primaryStage){
        Button PvBotButton = createButton("file:game/src/main/resources/pvbot_button.png");
        Button btnPlay = createButton("file:game/src/main/resources/play_button.png");
        Button PuzzleButton = createButton("file:game/src/main/resources/puzzle_button.png");
        
        
        Image backgroundImage = new Image("file:game/src/main/resources/background/background.png");
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,  new BackgroundSize(100, 100, true, true, false, true));
        btnPlay.setOnAction(event -> {
            gameMode = "PvP";
            //primaryStage.hide();
            gameLoop(primaryStage);
        });
        PvBotButton.setOnAction(event -> {
            gameMode = "PvB";
            //primaryStage.hide();
            gameLoop(primaryStage);
        });
        PuzzleButton.setOnAction(event ->{
            gameMode = "Puzzle";
            //primaryStage.hide();
            gameLoop(primaryStage);
        });
        
        VBox centerButtons = new VBox(0);
        centerButtons.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        centerButtons.getChildren().addAll(btnPlay, PvBotButton,PuzzleButton);
        centerButtons.setAlignment(Pos.CENTER);
        StackPane.setAlignment(centerButtons, Pos.CENTER);
        StackPane root= new StackPane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT );
        root.getChildren().addAll(canvas);
        Label nLabel = new Label("Start Game");
        nLabel.getStyleClass().add("label-title");
        HBox topTextLayout = new HBox(nLabel);
        topTextLayout.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        topTextLayout.setAlignment(Pos.TOP_CENTER);
        
        scene = new Scene(root);
        root.setBackground(new Background(bgImage));
        root.getChildren().addAll(topTextLayout,centerButtons);    
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    private Button createButton(String imagePath) {
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        if(imagePath == "file:game/src/main/resources/play_button.png"){
            imageView.setFitWidth(150);
            imageView.setFitHeight(60);
        }
        else
        { imageView.setFitWidth(150);
        imageView.setFitHeight(60);}
        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent;");
        
        return button;
    }


    private void gameLoop(Stage primaryStage1) {
        
        switch (gameMode) {
            case "PvP":
                // Game scene for PvP
                
                primaryStage1.setScene(null);
                primaryStage1.setTitle("Chess");
                primaryStage1.setResizable(false);
                StackPane root1 = new StackPane();
                Canvas canvas1 = new Canvas(WIDTH, HEIGHT);
                root1.getChildren().addAll(canvas1);
                root1.setStyle("-fx-background-color: red;");
                
                setCanvasHandlers(canvas1);
                
                GraphicsContext gc = canvas1.getGraphicsContext2D();
                GamePvP game = new GamePvP(gc, mouse, canvas1);
                game.gameloop();
                
                scene = new Scene(root1);
                primaryStage1.setScene(scene);
                //primaryStage1.show();
                
                break;
            
            case "PvB":
                // Game scene for PvB
                primaryStage1.setScene(null);
                primaryStage1.setTitle("Chess with PvB");
                primaryStage1.setResizable(false);
                StackPane root2 = new StackPane();
                Canvas canvas2 = new Canvas(WIDTH, HEIGHT);
                root2.getChildren().addAll(canvas2);
                root2.setStyle("-fx-background-color: black;");
                
                setCanvasHandlers(canvas2);
                
                GraphicsContext gc2 = canvas2.getGraphicsContext2D();
                GamePvB game2 = new GamePvB(gc2, mouse, canvas2);
                game2.gameloop();
                
                scene = new Scene(root2);
                primaryStage1.setScene(scene);
                //primaryStage1.show();
                
                break;
                
            case "Puzzle":
                // Game scene for Puzzle
                primaryStage1.setTitle("Chess with Puzzle");
                primaryStage1.setResizable(false);
                StackPane root3 = new StackPane();
                Canvas canvas3 = new Canvas(WIDTH, HEIGHT);
                root3.getChildren().addAll(canvas3);
                
                setCanvasHandlers(canvas3);
                
                GraphicsContext gc3 = canvas3.getGraphicsContext2D();
                Puzzle game3 = new Puzzle(gc3, mouse, canvas3);
                game3.gameloop();
                
                
                scene = new Scene(root3);
                primaryStage1.setScene(scene);
                //primaryStage1.show();
                
                break;
    
            default:
                throw new IllegalStateException("Unexpected value: " + gameMode);
        }
        

        // Add window close request handler to return to main menu
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();  // Prevent window from closing
            intMainmenu(primaryStage);  // Go back to main menu
        });
    }
    
    private void setCanvasHandlers(Canvas canvas) {
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
    }

}