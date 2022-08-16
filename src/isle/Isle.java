package isle;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Isle implements Runnable {

    private final Properties islandSet;
    private final Location[][] islandMap;
    private final ExecutorService executor;
    private int dayCounter = 1;

    public Isle(Properties islandSet) {
        this.islandSet = islandSet;
        this.islandMap = new Location[Integer.parseInt(islandSet.getProperty("SizeX"))]
                [Integer.parseInt(islandSet.getProperty("SizeY"))];
        this.executor = Executors.newFixedThreadPool(Integer.parseInt(islandSet.getProperty("ProcessorNumber")));
    }

    public void initialize() {
        int[] locationCoordinates;
        for (int i = 0; i < islandMap.length; i++) {
            for (int j = 0; j < islandMap[i].length; j++) {
                locationCoordinates = new int[]{i, j};
                islandMap[i][j] = new Location(islandSet, islandMap, locationCoordinates);
            }
        }
        System.out.println("День 1:");
    }

    @Override
    public void run() {
        dayCounter++;
        for (int i = 0; i < islandMap.length; i++) {
            for (int j = 0; j < islandMap[i].length; j++) {
                executor.execute(islandMap[i][j]);
            }
        }
        System.out.println("День: " + dayCounter);
        print();
        if (isExtinct()) {
            System.out.println("Все животные на острове вымерли. " +
                    "Жизнь на острове продолжалась " + dayCounter + " дней.");
            executor.shutdown();
            System.exit(0);
        }
    }

    public void print() {
        for (int i = 0; i < islandMap.length; i++) {
            for (int j = 0; j < islandMap[i].length; j++) {
                System.out.print("(" + "Локация " + i + "х" + j + ")" + islandMap[i][j]);
            }
            System.out.println();
        }
    }

    public boolean isExtinct() {
        int emptyLocationsCounter = 0;
        for (int i = 0; i < islandMap.length; i++) {
            for (int j = 0; j < islandMap[i].length; j++) {
                if (islandMap[i][j].getPopulation()
                        .entrySet()
                        .stream()
                        .allMatch(entry -> entry.getValue() <= 0)) {
                    emptyLocationsCounter++;
                }
            }
        }
        return emptyLocationsCounter == (islandMap.length * islandMap[0].length);
    }
}