package app.ui.overlay;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import app.phys.PlanetarySystem;
import app.phys.Probe;
import app.phys.trajectory.TrajectorySegment;
import app.ui.Landing_ui.LandingApp2D;
import app.ui.SpaceScene;
import app.ui.Trajectory;
import app.utils.PhysUtils;
import app.utils.Vector;
import app.utils.Vector3;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MissionMenu implements DropDown {
    private SpaceScene spaceScene;
    private PlanetarySystem planetarySystem;
    private Trajectory trajectory;
    private Menu menu = new Menu("Mission");

    private List<TrajectorySegment> mission2States;

    public MissionMenu(SpaceScene spaceScene) {
        this.spaceScene = spaceScene;
        this.planetarySystem = spaceScene.getPlanetarySystem();
        this.trajectory = spaceScene.getTrajectory();

        menu.getItems().add(getMission1Menu());
        menu.getItems().add(getMission2Menu());
        menu.getItems().add(getMission3Menu());

        mission2States = new ArrayList<>();
        deserializeMission2();
    }

    @SuppressWarnings("unchecked")
    private void deserializeMission2() {
        //try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("mission2.dat"))) {
        try (ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream("/mission2.dat"))) {
            mission2States = (List<TrajectorySegment>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Failed to deserialize mission2.dat: " + e.getMessage());
        }
    }

    public Menu getMenu() {
        return menu;
    }

    private void resetSim() {
        PlanetarySystem newSystem = new PlanetarySystem();
        for (int i = 0; i < planetarySystem.getPlanets().size(); i++) {
            planetarySystem.getPlanets().get(i).setPosition(newSystem.getPlanets().get(i).getPosition());
            planetarySystem.getPlanets().get(i).setVelocity(newSystem.getPlanets().get(i).getVelocity());
        }
        trajectory.clear();
        planetarySystem.updateState();
        planetarySystem.resetTime();
        spaceScene.setFlightPlan(null);
    }

    private MenuItem getMission1Menu() {
        MenuItem mission1Item = new MenuItem("Mission 1: Crash into Titan");
        mission1Item.setOnAction(event -> {
            resetSim();

            Vector launch = new Vector(new double[] { 2.574412603862625, -2.8869426836643965 });
            Vector3 direction = PhysUtils.fromAzimuthElevation(launch);
            Probe probe = planetarySystem.getProbe();
            probe.setPosition(planetarySystem.get("Earth").getPosition().clone()
                    .add(direction.clone().mul(planetarySystem.get("Earth").getRadius() + probe.getRadius())));
            probe.setVelocity(direction.clone().mul(60));
            planetarySystem.updateState();
        });
        return mission1Item;
    }

    private MenuItem getMission2Menu() {
        MenuItem missionItem = new MenuItem("Mission 2: Orbit Titan");
        missionItem.setOnAction(event -> {
            resetSim();
            spaceScene.setFlightPlan(mission2States);
            TrajectorySegment segment = mission2States.getFirst();
            Probe probe = planetarySystem.getProbe();
            probe.setPosition(planetarySystem.get(segment.center).getPosition().clone().add(segment.startPosRel));
            probe.setVelocity(segment.startVel);
            planetarySystem.updateState();
        });
        return missionItem;
    }

    private MenuItem getMission3Menu() {
        MenuItem missionItem = new MenuItem("Mission 3: Land on Titan");
        missionItem.setOnAction(event -> {
            resetSim();
            try {
                new LandingApp2D().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return missionItem;
    }
}
