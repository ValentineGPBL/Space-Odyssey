package app.phys;

import java.io.Serializable;

import app.utils.Vector3;
import javafx.util.Pair;

public class OrbitalParams implements Serializable {

    private final double epsilon = 1e-6; // epsilon for numerical stability
    private final double convergenceThreshold = 1e-10; // threshold for convergence in iterations

    private final double semiMajorAxis; // (a) size of the orbit
    private final double eccentricity; // (e) shape of the orbit
    private final double inclination; // (i) tilt of the orbit
    private final double longitudeOfAscendingNode; // (Ω) orientation of the orbit
    private final double argumentOfPeriapsis; // (ω) orientation of the ellipse
    private final double meanAnomaly; // (M) average position at epoch
    private final double mu; // (μ) standard gravitational parameter

    public OrbitalParams(double semiMajorAxis, double eccentricity, double inclination,
                         double longitudeOfAscendingNode, double argumentOfPeriapsis, double meanAnomaly, double mu) {
        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        this.meanAnomaly = meanAnomaly;
        this.mu = mu;
    }

    public OrbitalParams(Entity center, Vector3 position, Vector3 velocity) {
        this(
            Simulation.G * center.getMass(),
            position.clone().sub(center.getPosition()),
            velocity.clone().sub(center.getVelocity())
        );
    }

    public OrbitalParams(double mu, Vector3 relPos, Vector3 relVel) {
        this.mu = mu;
        Vector3 hVec = relPos.cross(relVel);
        Vector3 hVecNorm = hVec.clone().normalize();
        Vector3 eVec = relVel.cross(hVec).div(mu).sub(relPos.clone().normalize());

        double semiMajorAxis = -mu / (relVel.lengthSquared() - mu / relPos.length() * 2);
        double eccentricity = eVec.length();
        double inclination = Math.acos(hVec.getZ() / hVec.length());
        double argumentOfPeriapsis;
        double longitudeOfAscendingNode;
        double meanAnomaly;

        if(inclination < epsilon || Math.abs(inclination - Math.PI) < epsilon) { // Inclination is 0 or 180 degrees, i.e., parallel to the equatorial plane
            inclination = (inclination < epsilon) ? 0 : Math.PI;
            longitudeOfAscendingNode = 0; // By convention
            argumentOfPeriapsis = Math.atan2(eVec.getY(), eVec.getX());
        } else {
            Vector3 nVec = new Vector3(-hVecNorm.getY(), hVecNorm.getX(), 0).normalize();
            longitudeOfAscendingNode = Math.atan2(nVec.getY(), nVec.getX());
            argumentOfPeriapsis = Math.atan2(nVec.cross(eVec).dot(hVecNorm), nVec.dot(eVec));
        }
        if (argumentOfPeriapsis < 0) argumentOfPeriapsis += 2 * Math.PI;

        if(eccentricity < 1 - epsilon) {
            if(Math.abs(eccentricity) < epsilon) { // Circular orbit
                eccentricity = 0;
                argumentOfPeriapsis = 0; // By convention

                double cosO = Math.cos(longitudeOfAscendingNode);
                double sinO = Math.sin(longitudeOfAscendingNode);
                double cosI = Math.cos(inclination);
                double sinI = Math.sin(inclination);

                double xOrbital = relPos.getX() * cosO + relPos.getY() * sinO;
                double yOrbital = relPos.getX() * (-sinO * cosI) + relPos.getY() * cosO * cosI + relPos.getZ() * sinI;

                meanAnomaly = Math.atan2(yOrbital, xOrbital);
            } else { // Elliptical orbit
                double cosE = (semiMajorAxis - relPos.length()) / (semiMajorAxis * eccentricity);
                double sinE = (relPos.dot(relVel)) / (eccentricity * Math.sqrt(mu * semiMajorAxis));
                double E = Math.atan2(sinE, cosE);
                meanAnomaly = E - eccentricity * Math.sin(E);
            }
            if(meanAnomaly < 0) meanAnomaly += 2 * Math.PI;
        } else if(eccentricity > 1 + epsilon) { // Hyperbolic orbit
            double r = relPos.length();
            double dot_rv = relPos.dot(relVel);
            double coshH = (semiMajorAxis - r) / (semiMajorAxis * eccentricity);
            double sinhH = (dot_rv) / (eccentricity * Math.sqrt(mu * (-semiMajorAxis)));
            double H = Math.log(coshH + sinhH);
            meanAnomaly = eccentricity * Math.sinh(H) - H;
        } else { // Parabolic orbit
            throw new IllegalArgumentException("no parabolic orbits yet");
        }

        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        this.meanAnomaly = meanAnomaly;
    }

    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public double getInclination() {
        return inclination;
    }

    public double getLongitudeOfAscendingNode() {
        return longitudeOfAscendingNode;
    }

    public double getArgumentOfPeriapsis() {
        return argumentOfPeriapsis;
    }

    public double getMeanAnomaly() {
        return meanAnomaly;
    }

    public double getMu() {
        return mu;
    }

    public double getPeriod() {
        if (eccentricity < 1 - epsilon) { // Elliptical or Circular
            return 2 * Math.PI * Math.sqrt(Math.pow(semiMajorAxis, 3) / mu);
        } else if (eccentricity > 1 + epsilon) { // Hyperbolic
            return Double.POSITIVE_INFINITY; // Hyperbolic orbits do not have a period
        } else { // Parabolic
            throw new IllegalStateException("Parabolic orbits do not have a defined period.");
        }
    }

    public Pair<Vector3, Vector3> getState(double t) {
        if(semiMajorAxis == 0) {
            return new Pair<>(new Vector3(0, 0, 0), new Vector3(0, 0, 0));
        }

        double cosO = Math.cos(longitudeOfAscendingNode);
        double sinO = Math.sin(longitudeOfAscendingNode);
        double cosI = Math.cos(inclination);
        double sinI = Math.sin(inclination);
        double cosW = Math.cos(argumentOfPeriapsis);
        double sinW = Math.sin(argumentOfPeriapsis);

        double R11 = cosO * cosW - sinO * sinW * cosI;
        double R12 = -cosO * sinW - sinO * cosW * cosI;
        double R21 = sinO * cosW + cosO * sinW * cosI;
        double R22 = -sinO * sinW + cosO * cosW * cosI;
        double R31 = sinW * sinI;
        double R32 = cosW * sinI;

        if(eccentricity < 1  - epsilon) { // Elliptical or Circular
            double n = Math.sqrt(mu / Math.pow(semiMajorAxis, 3));

            double M = meanAnomaly + n * t;
            M = M % (2 * Math.PI);
            if (M < 0) M += 2 * Math.PI;

            double E = M;
            for (int i = 0; i < 50; i++) {
                double f = E - eccentricity * Math.sin(E) - M;
                if(Math.abs(f) < convergenceThreshold)
                    break;
                double fPrime = 1 - eccentricity * Math.cos(E);
                if(Math.abs(fPrime) < epsilon)
                    break;
                E -= f / fPrime;
            }

            double cosNu = (Math.cos(E) - eccentricity) / (1 - eccentricity * Math.cos(E));
            double sinNu = (Math.sqrt(1 - eccentricity * eccentricity) * Math.sin(E)) / (1 - eccentricity * Math.cos(E));
            double nu = Math.atan2(sinNu, cosNu);

            double r = semiMajorAxis * (1 - eccentricity * Math.cos(E));

            double xOrb = r * Math.cos(nu);
            double yOrb = r * Math.sin(nu);

            double sqrtMuA = Math.sqrt(mu * semiMajorAxis);
            double vxOrb = -sqrtMuA * Math.sin(E) / r;
            double vyOrb = sqrtMuA * Math.sqrt(1 - eccentricity * eccentricity) * Math.cos(E) / r;

            Vector3 pos = new Vector3(
                R11 * xOrb + R12 * yOrb,
                R21 * xOrb + R22 * yOrb,
                R31 * xOrb + R32 * yOrb
            );

            Vector3 vel = new Vector3(
                R11 * vxOrb + R12 * vyOrb,
                R21 * vxOrb + R22 * vyOrb,
                R31 * vxOrb + R32 * vyOrb
            );

            return new Pair<>(pos, vel);
        } else if (eccentricity > 1 + epsilon) {
            double n = Math.sqrt(-mu / Math.pow(semiMajorAxis, 3));

            double M = meanAnomaly + n * t;

            double H;
            if(Math.abs(M) > 10 * eccentricity) {
                H = Math.log(M / eccentricity + Math.sqrt((M/eccentricity)*(M/eccentricity) + 1));
            } else {
                H = Math.log(M/eccentricity + Math.sqrt(Math.pow(M/eccentricity, 2) + 1));
            }

            for (int i = 0; i < 50; i++) {
                double f = eccentricity * Math.sinh(H) - H - M;
                if (Math.abs(f) < convergenceThreshold)
                    break;
                double fPrime = eccentricity * Math.cosh(H) - 1;
                if (Math.abs(fPrime) < epsilon)
                    break;
                H -= f / fPrime;
                if(Double.isNaN(H)) {
                    throw new IllegalStateException("H became NaN during hyperbolic orbit calculation. Check parameters.");
                }
            }

            double nu = 2 * Math.atan(Math.sqrt((eccentricity + 1) / (eccentricity - 1)) * Math.tanh(H / 2));
            double r = semiMajorAxis * (1 - eccentricity * Math.cosh(H));
            
            double xOrb = r * Math.cos(nu);
            double yOrb = r * Math.sin(nu);

            double hMag = Math.sqrt(mu * (-semiMajorAxis) * (eccentricity * eccentricity - 1));
            double vxOrb = -(mu / hMag) * Math.sin(nu);
            double vyOrb = (mu / hMag) * (eccentricity + Math.cos(nu));

            Vector3 pos = new Vector3(
                R11 * xOrb + R12 * yOrb,
                R21 * xOrb + R22 * yOrb,
                R31 * xOrb + R32 * yOrb
            );

            Vector3 vel = new Vector3(
                R11 * vxOrb + R12 * vyOrb,
                R21 * vxOrb + R22 * vyOrb,
                R31 * vxOrb + R32 * vyOrb
            );

            return new Pair<>(pos, vel);
        } else { // Parabolic
            throw new IllegalStateException("don't support parabolic orbits yet");
        }
    }

    @Override
    public String toString() {
        return "OrbitalParams{" +
                "semiMajorAxis=" + semiMajorAxis +
                ", eccentricity=" + eccentricity +
                ", inclination=" + inclination +
                ", longitudeOfAscendingNode=" + longitudeOfAscendingNode +
                ", argumentOfPeriapsis=" + argumentOfPeriapsis +
                ", meanAnomaly=" + meanAnomaly +
                ", mu=" + mu +
                '}';
    }

}
