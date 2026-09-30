package app.ui;

import app.phys.CSVReader;
import app.phys.OrbitalParams;
import app.phys.Planet;
import app.phys.PlanetarySystem;
import app.utils.Vector3;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrbitVisualizer {
    private static final Color ORBIT_COLOR = Color.WHITE;
    private static final double ORBIT_WIDTH = 3e5;
    private static final double SCALE_FACTOR = 1e-3;
    private static final int SAMPLE_COUNT = 40000;

    /*
     * public Group createFromCSV(InputStream orbitStream) {
     * List<Vector3> positions = CSVReader.readOrbits(orbitStream);
     * return createOrbitLines(positions);
     * }
     */
    public Group createFromCSV(InputStream orbitStream, int planetCount) {
        Map<Integer, List<Vector3>> orbits = CSVReader.readPlanetOrbits(orbitStream, planetCount);
        Group group = new Group();
        for (List<Vector3> orbit : orbits.values()) {
            group.getChildren().add(createOrbitLines(orbit));
        }
        return group;
    }

    public Group createFromPlanetarySystem(PlanetarySystem system) {
        Group group = new Group();
        for (Planet planet : system.getPlanets()) {

            List<Vector3> positions = new ArrayList<>(SAMPLE_COUNT);
            if (planet.getOrbitalParams() != null) {
                OrbitalParams params = planet.getOrbitalParams();
                double period = params.getPeriod();
                if(Double.isNaN(period) || period <= 0)
                    continue;
                double step = period / SAMPLE_COUNT;
                System.out.println(period + " " + step + " " + SAMPLE_COUNT);
                for (double t = 0; t < period; t += step) {
                    Vector3 pos = params.getState(t).getKey();
                    positions.add(pos);
                }

                group.getChildren().add(createOrbitLines(positions));
            }
        }
        return group;
    }

    private Group createOrbitLines(List<Vector3> positions) {
        Group orbitGroup = new Group();
        Polyline orbit = new Polyline();

        orbit.setStroke(ORBIT_COLOR);
        orbit.setStrokeWidth(ORBIT_WIDTH / 200);
        orbit.setSmooth(true);

        int step = Math.max(1, positions.size() / 10000);

        for (int i = 0; i < positions.size(); i += step) {
            Vector3 pos = positions.get(i);
            orbit.getPoints().addAll(
                    pos.getX() * SCALE_FACTOR,
                    pos.getY() * SCALE_FACTOR);
        }
        // orbit.setTranslateZ(-1);
        orbit.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS),
                new Scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR));
        orbitGroup.getChildren().add(orbit);
        return orbitGroup;
    }
}