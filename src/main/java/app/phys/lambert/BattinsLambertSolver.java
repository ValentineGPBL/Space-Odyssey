package app.phys.lambert;

import app.phys.Entity;
import app.phys.Simulation;
import app.utils.PhysUtils;
import app.utils.Vector3;
import javafx.util.Pair;


// https://github.com/jorgepiloto/lamberthub/blob/main/src/lamberthub/p_solvers/battin.py
public class BattinsLambertSolver implements LambertSolver {

    private boolean prograde = true;
    private int maxiter = 75;
    private double atol = 1e-7;
    private double rtol = 1e-9;

    @Override
    public Pair<Vector3, Vector3> solve(Entity center, Vector3 r1, Vector3 r2, double tof) {
        double mu = center.getMass() * Simulation.G;
        double r1norm = r1.clone().length();
        double r2norm = r2.clone().length();
        double cnorm = r2.clone().sub(r1).length();
        double semiperimeter = (r1norm + r2norm + cnorm) / 2;
        double dtheta = PhysUtils.getTransferAngle(r1, r2, prograde);
        double lambda = getLambda(cnorm, semiperimeter, dtheta);
        double ll = getLl(lambda);
        double m = getM(mu, tof, semiperimeter, lambda);
        double T = Math.sqrt(8 * mu / Math.pow(semiperimeter, 3)) * tof;
        double Tp = (4.0 / 3.0) * (1 - Math.pow(lambda, 3));
        double x0 = T > Tp ? ll : 0;

        double x = x0;
        double y = 0;
        for (int iter = 0; iter < maxiter; iter++) {
            Pair<Double, Double> hCoeffs = getHCoefficients(x, ll, m);
            double h1 = hCoeffs.getKey();
            double h2 = hCoeffs.getValue();
            double u = uAtH(h1, h2);
            y = battinSecondEquation(u, h1, h2);
            x = battinFirstEquation(y, ll, m);
            if (Math.abs(x - x0) <= atol) {
                break;
            }
            x0 = x;
        }

        double r11 = Math.pow(1 + lambda, 2) / (4 * tof * lambda);
        double s11 = y * (1 + x);
        double t11 = (m * semiperimeter * Math.pow(1 + lambda, 2)) / s11;

        Vector3 term1 = r1.clone().sub(r2).mul(s11);
        Vector3 term2 = r1.clone().mul(t11 / r1norm);
        Vector3 v1 = term1.sub(term2).mul(-r11);

        Vector3 term3 = r1.clone().sub(r2).mul(s11);
        Vector3 term4 = r2.clone().mul(t11 / r2norm);
        Vector3 v2 = term3.add(term4).mul(-r11);

        return new Pair<>(v1, v2);
    }

    private double getLambda(double c, double s, double dtheta) {
        double lambda = Math.sqrt(s * (s - c)) / s;
        return dtheta < Math.PI ? Math.abs(lambda) : -Math.abs(lambda);
    }

    private double getLl(double lambda) {
        return Math.pow((1 - lambda) / (1 + lambda), 2);
    }

    private double getM(double mu, double tof, double s, double lambda) {
        return (8 * mu * tof * tof) / (Math.pow(s, 3) * Math.pow(1 + lambda, 6));
    }

    private Pair<Double, Double> getHCoefficients(double x, double ll, double m) {
        double xi = xiAtX(x);
        double denom = (1 + 2 * x + ll) * (4 * x + xi * (3 + x));
        double h1 = (Math.pow(ll + x, 2) * (1 + 3 * x + xi)) / denom;
        double h2 = (m * (x - ll + xi)) / denom;
        return new Pair<>(h1, h2);
    }

    private double xiAtX(double x) {
        int levels = 125;
        double eta = x / Math.pow(Math.sqrt(1 + x) + 1, 2);
        double delta = 0;
        double u = 1;
        double sigma = 1;
        int m1 = 1;

        while (Math.abs(u) > 1e-18 && m1 <= levels) {
            m1++;
            double gamma = Math.pow(m1 + 3, 2) / (4 * Math.pow(m1 + 3, 2) - 1);
            delta = 1.0 / (1 + gamma * eta * delta);
            u = u * (delta - 1);
            sigma += u;
        }

        return 8 * (Math.sqrt(1 + x) + 1) / (3 + 1.0 / (5 + eta + (9 * eta / 7) * sigma));
    }

    private double bAtH(double h1, double h2) {
        return (27 * h2) / (4 * Math.pow(1 + h1, 3));
    }

    private double uAtB(double B) {
        return -B / (2 * Math.sqrt(1 + B) + 1);
    }

    private double uAtH(double h1, double h2) {
        return uAtB(bAtH(h1, h2));
    }

    private double battinSecondEquation(double u, double h1, double h2) {
        double B = bAtH(h1, h2);
        double K = kAtU(u);
        return ((1 + h1) / 3.0) * (2 + Math.sqrt(B + 1) / (1 - 2 * u * K));
    }

    private double battinFirstEquation(double y, double ll, double m) {
        return Math.sqrt(Math.pow((1 - ll) / 2.0, 2) + m / (y * y)) - (1 + ll) / 2.0;
    }

    private double kAtU(double u) {
        int levels = 1000;
        double delta = 1;
        double u0 = 1;
        double sigma = 1;
        int n1 = 0;

        while (Math.abs(u0) > 1e-18 && n1 <= levels) {
            if (n1 == 0) {
                double gamma = 4.0 / 27.0;
                delta = 1.0 / (1 - gamma * u * delta);
                u0 = u0 * (delta - 1);
                sigma += u0;
            } else {
                for (int val = 1; val <= 2; val++) {
                    double gamma;
                    if (val == 1) {
                        gamma = (2 * (3 * n1 + 1) * (6 * n1 - 1)) / (9.0 * (4 * n1 - 1) * (4 * n1 + 1));
                    } else {
                        gamma = (2 * (3 * n1 + 2) * (6 * n1 + 1)) / (9.0 * (4 * n1 + 1) * (4 * n1 + 3));
                    }
                    delta = 1.0 / (1 - gamma * u * delta);
                    u0 = u0 * (delta - 1);
                    sigma += u0;
                }
            }
            n1++;
        }

        return Math.pow(sigma / 3.0, 2);
    }
}
