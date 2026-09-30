package app.ui.phase_1;

import app.solver.*;
import app.utils.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

public class ResultScreenController implements Initializable {

    @FXML
    private VBox resultsBox;

    @FXML
    private LineChart<Number, Number> lineChart;

    public void displayParams(String model, String solver, double x, double y, double z, double t0, double tf, double dt) {
        resultsBox.getChildren().add(new Label("Model: " + model));
        resultsBox.getChildren().add(new Label("Solver: " + solver));
        resultsBox.getChildren().add(new Label("Initial State: X=" + x + ", Y=" + y + ", Z=" + z));
        resultsBox.getChildren().add(new Label("t₀ = " + t0));
        resultsBox.getChildren().add(new Label("t𝒇 = " + tf));
        resultsBox.getChildren().add(new Label("dt = " + dt));

        plotSolutions(model, solver, x, y, z, t0, tf, dt);
    }

    private void plotSolutions(String model, String solver, double x, double y, double z, double t0, double tf, double dt) {
        BiFunction<Double, Vector, Vector> fn;
        Vector y0;
        String[] seriesNames;
        if (model.equals("Decay")) {
            fn = (t, yVal) -> yVal.clone().mul(-0.5);
            y0 = new Scalar(x).toVector();
            seriesNames = new String[]{"Decay"};
        } else if (model.equals("SIR")) {
            // Recommended values:
            // S = 990, I = 10, R = 0
            // x0 = 0, xf = 25, dt = 0.01
            fn = (t, yVal) -> {
                double S = yVal.get(0);
                double I = yVal.get(1);
                double R = yVal.get(2);
                double k = 3;
                double gamma = 1;
                double mu = 0.001; // Birth / Death rate

                return new Vector(new double[]{
                    -k * S * I + mu * (1 - S),
                    k * S * I - (gamma + mu) * I,
                    gamma * I - mu * R
                });
            };
            y0 = new Vector3(x, y, z).toVector();
            seriesNames = new String[]{"Susceptible", "Infected", "Recovered"};
        } else {
            return;
        }

        Solver<?> odeSolver;
        if (solver.equals("Euler")) {
            odeSolver = new Euler<Vector>(fn, dt, t0, y0);
        } else if (solver.equals("RK4")) {
            odeSolver = new RK4<Vector>(fn, dt, t0, y0);
        } else {
            return;
        }

        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
        for(String name : seriesNames) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(name);
            data.add(series);
        }

        while (odeSolver.getX() < tf) {
            var v = odeSolver.step();
            for (int i = 0; i < seriesNames.length; i++) {
                var series = data.get(i);
                series.getData().add(new XYChart.Data<>(odeSolver.getX(), v.get(i)));
            }
        }

        lineChart.setData(data);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Optional init
    }
}