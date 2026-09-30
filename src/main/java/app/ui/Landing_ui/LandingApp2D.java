package app.ui.Landing_ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import app.phys.LandingSimulation2D;

public class LandingApp2D extends Application {

    private LandingRenderer2D renderer;
    private LandingSimulation2D simulation;
    private Canvas canvas;
    // Lowered initial speed multiplier to start slower.
    private double timeMultiplier = 5.0;
    private AnimationTimer timer;

    @Override
    public void start(Stage primaryStage) {
        simulation = new LandingSimulation2D();
        canvas = new Canvas(1280, 800);
        renderer = new LandingRenderer2D(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight());

        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        HBox uiContainer = new HBox(10);
        uiContainer.setAlignment(Pos.CENTER_LEFT);
        uiContainer.setPadding(new Insets(10, 3.0, 10, 10));
        uiContainer.setMaxSize(240, 50);
        uiContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 5;");

        // Slider remains the same range.
        Slider speedSlider = new Slider(0.1, 26.0, timeMultiplier);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.setMaxWidth(200);
        speedSlider.setStyle("-fx-control-inner-background: lightgray; -fx-base: lightgray; -fx-tick-label-fill: white;");
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> timeMultiplier = newVal.doubleValue());

        Button resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> simulation = new LandingSimulation2D());

        uiContainer.getChildren().addAll(speedSlider, resetBtn);
        StackPane.setAlignment(uiContainer, Pos.BOTTOM_LEFT);
        StackPane.setMargin(uiContainer, new Insets(10));
        root.getChildren().add(uiContainer);

        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setTitle("Titan Landing Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        timer = new AnimationTimer() {
            private long lastUpdate = System.nanoTime();
            @Override
            public void handle(long now) {
                double deltaTime = (now - lastUpdate) / 1e9;
                simulation.update(deltaTime * timeMultiplier);
                renderer.render(simulation.getProbe().getPosition());
                lastUpdate = now;
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}