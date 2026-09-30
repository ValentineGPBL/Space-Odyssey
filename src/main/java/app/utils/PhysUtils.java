package app.utils;

import app.phys.Entity;
import app.phys.PlanetarySystem;
import app.phys.Simulation;

public class PhysUtils {
    public static boolean DEBUG = false;
    public static final double PROBE_SPEED = 60;

    public static Vector toAzimuthElevation(Vector3 v) {
        double azimuth = Math.atan2(v.get(1), v.get(0));
        double elevation = Math.atan2(v.get(2), Math.sqrt(v.get(0) * v.get(0) + v.get(1) * v.get(1)));
        return new Vector(new double[] {azimuth, elevation});
    }

    public static Vector3 fromAzimuthElevation(Vector x) {
        double azimuth = x.get(0);
        double elevation = x.get(1);
        double cosElev = Math.cos(elevation);
        return new Vector3(
            Math.cos(azimuth) * cosElev,
            Math.sin(azimuth) * cosElev,
            Math.sin(elevation)
        );
    }

    public static Vector computeInitialGuess(PlanetarySystem pSystem, int startIndex, int targetIndex) {
        PlanetarySystem system = pSystem.clone();
        Entity startObj = system.get(startIndex);
        Entity targetObj = system.get(targetIndex);

        Vector3 startPoint = startObj.getPosition();
        // How far are we from our target object?
        double dist = startObj.getDistance(targetObj) - startObj.getRadius() - targetObj.getRadius();
        // How long (in seconds) does it take to get there
        double time = dist / PROBE_SPEED; // 60 km/s
        // Where is our target after the travel time?
        system.advance(time);
        Vector3 targetPos = targetObj.getPosition();
        // In which direction should we shoot off to intercept our target at that point in time?
        Vector3 targetVector3 = targetPos.sub(startPoint).normalize();
        // Which angle is this?
        Vector initState = toAzimuthElevation(targetVector3);

        return initState;
    }

    public static double getTransferAngle(Vector3 r1, Vector3 r2, boolean prograde) {
        if(r1.cross(r2).equals(new Vector3())) {
            if(
                Math.signum(r1.getX()) == Math.signum(r2.getX()) &&
                Math.signum(r1.getY()) == Math.signum(r2.getY()) &&
                Math.signum(r1.getZ()) == Math.signum(r2.getZ())
            ) {
                return 0;
            } else {
                return Math.PI;
            }
        }

        Vector3 h = r1.cross(r2).div(r1.cross(r2).length());
        double alpha = new Vector3(0, 0, 1).dot(h);
        double r1norm = r1.length();
        double r2norm = r2.length();
        double theta0 = Math.acos(r1.dot(r2) / (r1norm * r2norm));

        double dtheta;
        if(prograde) {
            dtheta = alpha > 0 ? theta0 : (2 * Math.PI - theta0);
        } else {
            dtheta = alpha < 0 ? theta0 : (2 * Math.PI - theta0);
        }

        return dtheta;
    }

    /**
     * Escape velocity at a given distance from the center of mass.
     */
    public static double escapeVel(double mu, double r) {
        // v^2 = 2*mu / r
        return Math.sqrt(2*mu / r);
    }
    public static double escapeVel(Entity entity, double r) {
        return escapeVel(entity.getMass() * Simulation.G, r);
    }

    /**
     * Escape velocity with excess hyperbolic velocity at infinity.
     */
    public static double escapeVelWithExcess(double mu, double r, double vInf) {
        // v^2 = v^2_esc + v^2_inf
        return Math.sqrt(2 * mu / r + vInf * vInf);
    }
    public static double escapeVelWithExcess(Entity entity, double r, double vInf) {
        return escapeVelWithExcess(entity.getMass() * Simulation.G, r, vInf);
    }

    /**
     * Excess hyperbolic velocity at infinity.
     */
    public static double excessHyperbolicVel(double mu, double r, double v) {
        // v_inf = sqrt(v^2 - v^2_esc)
        double vEsc = escapeVel(mu, r);
        if (v < vEsc) {
            throw new IllegalArgumentException("Velocity must be greater than escape velocity.");
        }
        return Math.sqrt(v * v - vEsc * vEsc);
    }
    public static double excessHyperbolicVel(Entity entity, double r, double v) {
        return excessHyperbolicVel(entity.getMass() * Simulation.G, r, v);
    }

    /**
     * Speed at a given distance from the center of mass.
     */
    public static double speedAtDistance(double mu, double r) {
        // v = sqrt(mu / r)
        return Math.sqrt(mu / r);
    }
    public static double speedAtDistance(Entity entity, double r) {
        return speedAtDistance(entity.getMass() * Simulation.G, r);
    }
}
