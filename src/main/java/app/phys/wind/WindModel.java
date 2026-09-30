package app.phys.wind;

import app.utils.Vector3;

public interface WindModel {

    /**
     * @param time      current simulation time (s)
     * @param position  current rocket position (km)
     * @param velocity  current rocket velocity (km/s)
     * @return           a 3D wind‐induced acceleration (km/s²)
     */
    Vector3 getWindAcceleration(double time, Vector3 position, Vector3 velocity);
}
