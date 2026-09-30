package app.phys.landing;

import app.phys.Entity;
import app.phys.Planet;
import app.phys.Probe;
import app.phys.Simulation;
import app.utils.Vector3;

public class FeedbackController {
    Probe probe;
    Planet planet;

    public FeedbackController(Probe probe, Planet planet) {
        this.probe = probe;
        this.planet = planet;
    }

    public void applyCorrection() {
        double desiredAngle = getDesiredAngle();
        double desiredRotation = probe.getAngle() + probe.getAngularVelocity() - desiredAngle;
        probe.applyRotation(-desiredRotation);
    }

    private double getDesiredAngle() {
        Vector3 titanToProbeVector = probe.getPosition().clone().sub(planet.getPosition());
        Vector3 titanToProbeNormal = titanToProbeVector.clone().normalize();
        Vector3 gravityVelocity = titanToProbeNormal.mul(-(Simulation.G * planet.getMass())/Math.pow(titanToProbeVector.length(), 2));
        Vector3 nonGravityVelocity = probe.getVelocity().clone().sub(gravityVelocity);
        System.out.println(probe.getVelocity());
        System.out.println(gravityVelocity);
        
        if (Math.sqrt(gravityVelocity.length()) < 0.032) {
            double x = nonGravityVelocity.getX();
            double y = nonGravityVelocity.getZ();
            double angle = Math.atan2(y, x) + Math.PI/2;
            return angle;
        }
        else {
            double x = probe.getPosition().getX() - planet.getPosition().getX();
            double y = probe.getPosition().getZ() - planet.getPosition().getZ();
            double angle = Math.atan2(y, x) + Math.PI/2;
            return angle;
        }
    }

    private double getDistanceToPlanetSurface() {
        return planet.getPosition().clone().sub(probe.getPosition()).length() - planet.getRadius();
    }
}
