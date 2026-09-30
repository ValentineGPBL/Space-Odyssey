package app.phys.trajectory;

import app.utils.Vector3;

public class FlybyState {
    
    public final String target;
    public final double altitude;

    public FlybyState(String target) {
        this.target = target;
        this.altitude = 0.0;
    }

    public FlybyState(String target, double altitude) {
        this.target = target;
        this.altitude = altitude;
    }

    public FlybyState(String target, double altitude, Vector3 normal, double time) {
        this.target = target;
        this.altitude = altitude;
    }

}
