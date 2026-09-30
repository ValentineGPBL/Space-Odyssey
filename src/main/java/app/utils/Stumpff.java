package app.utils;

public class Stumpff {
    
    public static double C(double z) {
        if (z > 0) {
            return (1 - Math.cos(Math.sqrt(z))) / z;
        } else if (z < 0) {
            return (Math.cosh(Math.sqrt(-z)) - 1) / (-z);
        } else {
            return 0.5; // C(0) = 0.5
        }
    }

    public static double S(double z) {
        if (z > 0) {
            return (Math.sqrt(z) - Math.sin(Math.sqrt(z))) / Math.pow(z, 1.5);
        } else if (z < 0) {
            return (Math.sinh(Math.sqrt(-z)) - Math.sqrt(-z)) / Math.pow(-z, 1.5);
        } else {
            return 1.0 / 6.0; // S(0) = 1/6
        }
    }

}
