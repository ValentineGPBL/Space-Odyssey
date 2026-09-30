package app.ui.overlay;

import app.phys.PlanetarySystem;
import app.ui.SpaceScene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.Slider;

public class SimulationMenu implements DropDown {
    private SpaceScene spaceScene;
    private PlanetarySystem planetarySystem;
    private Menu menu = new Menu("Simulation");
    private Slider speedSlider;

    public SimulationMenu(SpaceScene spaceScene) {
        this.spaceScene = spaceScene;
        this.planetarySystem = spaceScene.getPlanetarySystem();
        
        menu.getItems().add(getSpeedMenu());
        menu.getItems().add(getPauseMenu());
    }

    public Menu getMenu() {
        return menu;
    }

    private CheckMenuItem getPauseMenu() {
        CheckMenuItem checkItem = new CheckMenuItem("Pause simulation");
        checkItem.setOnAction(event -> {
            if (checkItem.isSelected()) {
                spaceScene.setStepSize(0);
                speedSlider.setDisable(true);
            }
            else {
                spaceScene.setStepSize((int)speedSlider.getValue());
                speedSlider.setDisable(false);
            }
        });

        return checkItem;
    }

    private Menu getSpeedMenu() {
        Label speedLabel = new Label("1 simulation hours per second");
        CustomMenuItem labelItem = new CustomMenuItem(speedLabel);

        speedSlider = new Slider(1, 1000, 1);
        speedSlider.setPrefWidth(300);
        speedSlider.setMajorTickUnit(60);
        speedSlider.setSnapToTicks(true);
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            speedSlider.setValue(Math.round(newValue.doubleValue()));
            spaceScene.setStepSize((int)speedSlider.getValue());
            speedLabel.setText(speedSlider.getValue() + " times faster");
        });
        CustomMenuItem sliderItem = new CustomMenuItem(speedSlider);

       Menu speedMenu = new Menu("Speed");
       speedMenu.getItems().addAll(labelItem, sliderItem);

       return speedMenu;
    }
}
