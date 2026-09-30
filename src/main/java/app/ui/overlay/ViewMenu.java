package app.ui.overlay;

import java.util.ArrayList;

import app.ui.SpaceProbe;
import app.ui.SpaceScene;
import app.ui.Trajectory;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.Slider;
import javafx.scene.shape.Sphere;

public class ViewMenu implements DropDown{
    private ArrayList<Sphere> spaceSpheres;
    private ArrayList<Double> sphereRadius = new ArrayList<Double>();
    private SpaceProbe spaceProbe;
    private Trajectory trajectory;

    private Menu menu = new Menu("View");

    public ViewMenu(SpaceScene spaceScene) {
        this.spaceSpheres = spaceScene.getSpaceSpheres().getSpheres();
        for (Sphere sphere : spaceSpheres) {
            sphereRadius.add(sphere.getRadius());
        }
        this.spaceProbe = spaceScene.getSpaceProbe();
        this.trajectory = spaceScene.getTrajectory();

        menu.getItems().add(getPlanetSizeMenu());
        menu.getItems().add(getSpaceshipSizeMenu());
    }

    public Menu getMenu() {
        return menu;
    }

    public Menu getPlanetSizeMenu() {
        Label sizeLabel = new Label("x1.0 planet size multiplier");
        CustomMenuItem labelItem = new CustomMenuItem(sizeLabel);

        Slider sizeSlider = new Slider(1, 500, 1);
        sizeSlider.setPrefWidth(300);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            for (int i = 0; i < spaceSpheres.size(); i++) {
                spaceSpheres.get(i).setRadius(Math.min(sphereRadius.get(i) * sizeSlider.getValue(), i == 0 ? 50 : 500));
            }
            double ratio = sizeSlider.getValue();
            sizeLabel.setText("x" + String.format("%.1f", ratio) + " planet size multiplier");
        });
        CustomMenuItem sliderItem = new CustomMenuItem(sizeSlider);

       Menu sizeMenu = new Menu("Planet Size");
       sizeMenu.getItems().addAll(labelItem, sliderItem);

       return sizeMenu;
    }

    public Menu getSpaceshipSizeMenu() {
        Label sizeLabel = new Label("x1.0 spaceship size multiplier");
        CustomMenuItem labelItem = new CustomMenuItem(sizeLabel);

        Slider sizeSlider = new Slider(1, 1000, 1);
        sizeSlider.setPrefWidth(300);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            /*for (Sphere sphere : spaceSpheres) {
                if (sphere.getId().equals("probe")) {
                    sphere.setRadius(sphereRadius.get(0) * sizeSlider.getValue());
                }
            }
            double ratio = sizeSlider.getValue();*/
            sizeLabel.setText("x" + String.format("%.1f", sizeSlider.getValue()) + " spaceship size multiplier");
            spaceProbe.setScale(sizeSlider.getValue());
            trajectory.setScale(sizeSlider.getValue());
        });
        CustomMenuItem sliderItem = new CustomMenuItem(sizeSlider);

       Menu sizeMenu = new Menu("Spaceship Size");
       sizeMenu.getItems().addAll(labelItem, sliderItem);

       return sizeMenu;
    }

}
