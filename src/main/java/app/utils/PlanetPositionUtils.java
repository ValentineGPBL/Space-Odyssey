package app.utils;

import app.phys.Planet;

/**
 * A class intended to be used as a parameter object
 */
public class PlanetPositionUtils {
    private Planet planet;
    private double azimuth;
    private double elevation;

    public PlanetPositionUtils(Planet planet, double azimuth, double elevation) {
        this.planet = planet;
        this.azimuth = azimuth;
        this.elevation = elevation;
    }

    /**
     * returns the position in the surface of the planet
     * @return
     */
    public Vector3 getSurfacePosition() {
        Vector azimuthElevation = new Vector(2);
        azimuthElevation.set(0, azimuth);
        azimuthElevation.set(1, elevation);

        Vector3 globalPosition = planet.getPosition().clone();
        globalPosition.add(PhysUtils.fromAzimuthElevation(azimuthElevation));

        return globalPosition;
    }

    /**
     * returns the position of the planet
     * @return
     */
    public Vector3 getCenterPosition() {
        return planet.getPosition();
    }
}
