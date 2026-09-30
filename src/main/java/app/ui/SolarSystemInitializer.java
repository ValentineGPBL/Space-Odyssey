package app.ui;

import java.io.InputStream;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import app.phys.OrbitalParams;
import app.phys.Planet;
import app.phys.Simulation;
import app.utils.Vector3;

public class SolarSystemInitializer {

    public static final double REL_TIME = Duration.between(
        ZonedDateTime.of(2000, 1, 1, 11, 58, 55, 816000000, ZoneOffset.UTC).toInstant(),
        ZonedDateTime.of(2025, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant()
    ).toSeconds();

    /**
     * Loads a CSV containing planet data.
     * Data must be formatted as follows:
     * Name, Position, Velocity, Mass, Radius
     * @param stream An InputStream to a CSV file containing the data
     * @return A list of planets generated from the given CSV file
     */
    public static ArrayList<Planet> parseBodyList(InputStream stream) {
        ArrayList<Planet> bodies = new ArrayList<>();
        Scanner s = new Scanner(stream);
        // Set the scanner to use a pattern that matches both LF and CRLF
        s.useDelimiter(",|\\r\\n|\\n");
        s.useLocale(Locale.US);
        for(int x = 0; x < 22; x++) s.next(); // Skip header
        while(s.hasNext()) {
            String name = s.next();
            Vector3 position = new Vector3(s.nextDouble(), s.nextDouble(), s.nextDouble());
            Vector3 velocity = new Vector3(s.nextDouble(), s.nextDouble(), s.nextDouble());
            double mass = s.nextDouble();
            double radius = s.nextDouble();
            String parent = s.next();
            /*if(bodies.size() > 0) {
                // For other planets, exaggerate the radius more
                radius *= 500;
            } else {
                radius *= 25;
            }*/

            double eccentricity = s.nextDouble();
            s.nextDouble(); // Periapsis distance
            double inclination = Math.toRadians(s.nextDouble());
            double longAscNode = Math.toRadians(s.nextDouble());
            double argPerifocus = Math.toRadians(s.nextDouble());
            s.nextDouble(); // Time of periapsis
            s.nextDouble(); // Mean motion
            double meanAnomaly = Math.toRadians(s.nextDouble());
            s.nextDouble(); // True anomaly
            double semiMajorAxis = s.nextDouble();
            s.nextDouble(); // Apoapsis distance
            s.nextDouble(); // Sidereal orbit period

            //meanAnomaly = (meanAnomaly - Math.sqrt(Simulation.G * mass / Math.pow(semiMajorAxis, 3))) % (2 * Math.PI);

            OrbitalParams params = new OrbitalParams(
                semiMajorAxis, eccentricity, inclination,
                longAscNode, argPerifocus, meanAnomaly,
                Simulation.G * mass
            );

            bodies.add(new Planet(name, position, velocity, mass, radius, 
                bodies.stream().filter(p -> parent.equals(p.getName())).findFirst().orElse(null)
                , params
            ));
        }
        s.close();

        return bodies;
    }
}