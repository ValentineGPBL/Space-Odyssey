package app.ui;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import app.phys.Entity;
import app.phys.OrbitalParams;
import app.phys.PlanetarySystem;
import app.phys.Probe;
import app.phys.lambert.BattinsLambertSolver;
import app.phys.lambert.LambertSolver;
import app.phys.trajectory.CrashTrajectoryStrategy;
import app.phys.trajectory.FlybyState;
import app.phys.trajectory.TrajectorySegment;
import app.ui.overlay.Overlay;
import app.utils.UIUtils;
import app.utils.Vector;
import app.utils.Vector3;

public class SpaceScene extends Application {
    private static final int PLANET_COUNT = 12;
    private PlanetarySystem planetarySystem = new PlanetarySystem();
    private Probe sysProbe = planetarySystem.getProbe();
    private SpaceCamera planetCamera = new SpaceCamera(planetarySystem);
    private Scene scene;
    private Group planetGroup;
    private SpaceSpheres spaceSpheres = new SpaceSpheres();
    private Trajectory probeTrajectory = new Trajectory();
    private SpaceProbe probe;
    private int stepSize = 1;
    private List<TrajectorySegment> flightPlan = new ArrayList<>();

    public SpaceSpheres getSpaceSpheres() {
        return spaceSpheres;
    }

    public PlanetarySystem getPlanetarySystem() {
        return planetarySystem;
    }

    public SpaceCamera getSpaceCamera() {
        return planetCamera;
    }

    public Scene getScene() {
        return scene;
    }

    public Group getPlanetGroup() {
        return planetGroup;
    }

    public Trajectory getTrajectory() {
        return probeTrajectory;
    }

    public SpaceProbe getSpaceProbe() {
        return probe;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public void setFlightPlan(List<TrajectorySegment> flightPlan) {
        this.flightPlan = flightPlan;
    }

    @Override
    public void start(Stage stage) {
        planetarySystem.setStepSize(25);
        // sysProbe.setPosition(new Vector3(-4e7, -4e7, 0));
        CrashTrajectoryStrategy trajectoryStrategy = new CrashTrajectoryStrategy(planetarySystem,
                planetarySystem.get("Earth"),
                planetarySystem.get("Titan"), sysProbe);
        trajectoryStrategy.setupProbe(planetarySystem,
                new Vector(new double[] { 2.5830974344000004, -2.9057312832000006 }));
        planetarySystem.updateState();

        // Planet subscene setup
        planetGroup = new Group();
        SubScene planetScene = new SubScene(planetGroup, 1280, 800, true, SceneAntialiasing.BALANCED);

        planetScene.setFill(Color.BLACK);
        // camera.lockToSphere((Sphere) root.getChildren().get(3)); // Lock to the Sun
        planetScene.setCamera(planetCamera);

        // Add planets
        List<Entity> planetEntities = new ArrayList<>(planetarySystem.getPlanets());
        Group planetBodies = spaceSpheres.createCelestialBodies(planetEntities);
        planetGroup.getChildren().add(planetBodies);

        // Add orbits
        try (InputStream orbitStream = getClass().getResourceAsStream("/orbits.csv")) {
            if (orbitStream != null) {
                OrbitVisualizer visualizer = new OrbitVisualizer();
                Group orbitsGroup = visualizer.createFromCSV(orbitStream, PLANET_COUNT);
                // Group orbitsGroup = visualizer.createFromPlanetarySystem(planetarySystem);
                orbitsGroup.setDepthTest(DepthTest.DISABLE);
                planetGroup.getChildren().add(0, orbitsGroup); // Add behind planets
            } else {
                System.err.println("Could not load orbits.csv");
            }
        } catch (Exception e) {
            System.err.println("Error loading orbits: " + e.getMessage());
        }

        // Add probe
        probe = new SpaceProbe(sysProbe.getRadius() * 1e6);
        probe.updateProbePosition(sysProbe.getPosition());

        planetBodies.getChildren().add(probe.getProbe());
        planetBodies.getChildren().add(probeTrajectory.getTrajectory());

        // Overlay setup
        /*
         * Pair<StackPane, SpaceOverlay> overlayPair = UIUtils.loadFXML("SpaceOverlay");
         * overlayPair.getValue().initialize(scene, planetarySystem.getEntities(),
         * (entity) -> {
         * System.out.println(entity.getName() + " selected");
         * planetCamera.track(entity);
         * });
         */

        // Main scene setup
        Overlay overlay = new Overlay(this);
        BorderPane root = overlay.getBorderPane();

        // root.setRight(overlayPair.getKey());
        root.setCenter(planetScene);

        scene = new Scene(root, 1280, 800);
        scene.widthProperty().addListener((obs, oldVal, newVal) -> planetScene.setWidth(newVal.doubleValue()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> planetScene.setHeight(newVal.doubleValue()));
        planetCamera.enableFreeLook(scene);

        // Setup stage
        stage.setTitle("Space Scene");
        stage.setScene(scene);
        stage.show();

        // Animation loop
        new AnimationTimer() {
            Vector3 oldProbePos = sysProbe.getPosition();
            int x = 0;
            LambertSolver solver = new BattinsLambertSolver();

            @Override
            public void handle(long now) {
                for (int i = 0; i < stepSize; i++) {
                    if (flightPlan != null) {
                        for (TrajectorySegment segment : flightPlan) {
                            if (segment.startTime <= planetarySystem.getTime()
                                    && segment.endTime >= planetarySystem.getTime()) {
                                Vector3 currentRelPos = sysProbe.getPosition().clone()
                                        .sub(planetarySystem.get(segment.center).getPosition());
                                Vector3 targetRelPos = segment.orbitalParams
                                        .getState(planetarySystem.getTime() - segment.startTime).getKey();
                                double error = currentRelPos.clone().sub(targetRelPos).length();
                                if (error >= 1000) {
                                    // System.out.println("In-flight adjustment. currentRelPos: " + currentRelPos +
                                    // ", targetRelPos: " + targetRelPos + ", error: " + error);
                                    Entity center = planetarySystem.get(segment.center);
                                    // OrbitalParams newCenterParams = new OrbitalParams(center,
                                    // center.getPosition(), center.getVelocity());
                                    Vector3 newVel = solver.solve(center, currentRelPos, segment.endPosRel,
                                            segment.endTime - planetarySystem.getTime()).getKey();
                                    Vector3 velDiff = newVel
                                            .sub(sysProbe.getVelocity().clone().sub(center.getVelocity()));
                                    sysProbe.applyThrust(velDiff);
                                    // System.out.println(sysProbe.getFuelConsumed());
                                    // if(sysProbe.getFuelConsumed() > 100)
                                    // System.exit(0);
                                    planetarySystem.updateState();

                                    try {
                                        OrbitalParams params = new OrbitalParams(center, sysProbe.getPosition(),
                                                sysProbe.getVelocity());
                                        segment.orbitalParams = params;
                                    } catch (Exception e) {
                                    }

                                    // System.exit(0);
                                }
                            }
                        }
                    }
                    planetarySystem.step();
                }
                for (int i = 0; i < planetEntities.size(); i++) {
                    Entity body = planetEntities.get(i);
                    body.apply(planetBodies.getChildren().get(i));
                }
                // Update probe position
                probe.updateProbePosition(sysProbe.getPosition());
                sysProbe.setOrientation(sysProbe.getVelocity().clone().normalize());
                UIUtils.lookAt(probe.getProbe(), sysProbe.getPosition().clone().add(sysProbe.getOrientation()));

                planetCamera.handleUpdate();
                if (x++ % 30 == 0) {
                    probeTrajectory.createSegment(sysProbe.getPosition(), oldProbePos);
                    oldProbePos = sysProbe.getPosition();
                }
            }
        }.start();
    }
}
