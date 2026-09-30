package app.phys;

import app.utils.Vector3;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;  
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class CSVReader {

    public static List<Vector3> readOrbits(InputStream stream) {
        List<Vector3> positions = new ArrayList<>();
        Scanner scanner = new Scanner(stream);
        
        scanner.useDelimiter(",|\\r\\n|\\n");
        scanner.useLocale(Locale.US);
        
        while (scanner.hasNextDouble()) {
            double x = scanner.nextDouble();
            double y = scanner.nextDouble();
            double z = scanner.nextDouble();
            positions.add(new Vector3(x, y, z));
        }
        
        scanner.close();
        return positions;
    }
public static Map<Integer, List<Vector3>> readPlanetOrbits(InputStream stream, int planetCount) {
    Map<Integer, List<Vector3>> orbits = new java.util.HashMap<>();
    for (int i = 0; i < planetCount; i++) {
        orbits.put(i, new ArrayList<>());
    }

    Scanner scanner = new Scanner(stream);
    scanner.useDelimiter(",|\\r\\n|\\n");
    scanner.useLocale(Locale.US);

    int currentPlanet = 0;
    while (scanner.hasNextDouble()) {
        double x = scanner.nextDouble();
        double y = scanner.nextDouble();
        double z = scanner.nextDouble();

        orbits.get(currentPlanet).add(new Vector3(x, y, z));
        currentPlanet = (currentPlanet + 1) % planetCount;
    }

    scanner.close();
    return orbits;
}

    public static void main(String[] args) {
        try {
            InputStream stream = CSVReader.class.getResourceAsStream("/orbits.csv");
            if (stream == null) {
                System.err.println("Error: orbits.csv not found in resources!");
                return;
            }
            
            List<Vector3> positions = readOrbits(stream);
            
            System.out.printf("%nTotal positions loaded: %d%n", positions.size());
            
        } catch (Exception e) {
            System.err.println("Error reading CSV:");
            e.printStackTrace();
        }
    }
} 