package app.ui.phase_1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class InputScreenController {

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private ComboBox<String> solverComboBox;

    @FXML
    private TextField inputX;

    @FXML
    private TextField inputY;

    @FXML
    private TextField inputZ;

    @FXML
    private TextField inputT0;

    @FXML
    private TextField inputTf;

    @FXML
    private TextField inputDt;

    @FXML
    private Button runButton;

    @FXML
    public void initialize() {
        modelComboBox.getItems().addAll("Decay", "SIR");
        solverComboBox.getItems().addAll("Euler", "RK4");
        modelComboBox.getSelectionModel().selectFirst();
        solverComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void onRunSimulation(ActionEvent event) {
        String model = modelComboBox.getValue();
        String solver = solverComboBox.getValue();

        double x = Double.parseDouble(inputX.getText());
        double y = Double.parseDouble(inputY.getText());
        double z = Double.parseDouble(inputZ.getText());
        double t0 = Double.parseDouble(inputT0.getText());
        double tf = Double.parseDouble(inputTf.getText());
        double dt = Double.parseDouble(inputDt.getText());

        // Print to console
        System.out.println("Running " + model + " with " + solver);
        System.out.println("Initial state: [" + x + ", " + y + ", " + z + "]");
        System.out.println("t0=" + t0 + ", tf=" + tf + ", dt=" + dt);

        // Show result screen with passed values
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResultScreen.fxml"));
            Parent root = loader.load();

            // Get the controller and pass data
            ResultScreenController controller = loader.getController();
            controller.displayParams(model, solver, x, y, z, t0, tf, dt);

            // Get current stage from button
            Stage stage = (Stage) runButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
