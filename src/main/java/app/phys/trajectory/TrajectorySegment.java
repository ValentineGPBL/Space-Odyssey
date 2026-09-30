package app.phys.trajectory;

import java.io.Serializable;

import app.phys.Entity;
import app.phys.OrbitalParams;
import app.phys.PlanetarySystem;
import app.phys.Simulation;
import app.utils.Vector3;

public class TrajectorySegment implements Serializable {
    
    public Vector3 startVel;
    public Vector3 endVel;
    public Vector3 startPosRel;
    public Vector3 endPosRel;
    public double startTime;
    public double tof;
    public double endTime;
    public String center;
    public OrbitalParams orbitalParams;

    public TrajectorySegment(Vector3 startVel, Vector3 endVel, Vector3 startPosRel, Vector3 endPosRel, double startTime, double endTime, String center) {
        this.startVel = startVel;
        this.endVel = endVel;
        this.startPosRel = startPosRel;
        this.endPosRel = endPosRel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.center = center;
        this.tof = endTime - startTime;
        Entity centerEntity = new PlanetarySystem().get(center);
        this.orbitalParams = new OrbitalParams(centerEntity.getMass() * Simulation.G, startPosRel, startVel.clone().sub(centerEntity.getVelocity()));
    }

}
