package alive;

import isle.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum Movement {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    private static final List<Movement> DIRECTION =
            Collections.unmodifiableList(Arrays.asList(values()));


    public static Location getNewLocation(Location location, int moveSpeed) {
        Movement movement = DIRECTION.get(ThreadLocalRandom.current().nextInt(4));

        Location tempLocation = location;
        Location newLocation = location;
        for (int i = 0; i < moveSpeed; i++) {
            int[] oldCoordinates = tempLocation.getLocationCoordinates();
            if (movement == Movement.UP) {
                if (oldCoordinates[1] > 0) {
                    newLocation = location.getIslandMap()[oldCoordinates[0]][oldCoordinates[1] - 1];
                }
            } else if (movement == Movement.RIGHT) {
                if (oldCoordinates[0] < location.getIslandMap().length - 1) {
                    newLocation = location.getIslandMap()[oldCoordinates[0] + 1][oldCoordinates[1]];
                }
            } else if (movement == Movement.DOWN) {
                if (oldCoordinates[1] < location.getIslandMap()[0].length - 1) {
                    newLocation = location.getIslandMap()[oldCoordinates[0]][oldCoordinates[1] + 1];
                }
            } else if (movement == Movement.LEFT) {
                if (oldCoordinates[0] > 0) {
                    newLocation = location.getIslandMap()[oldCoordinates[0] - 1][oldCoordinates[1]];
                }
            }
            tempLocation = newLocation;
        }

        return newLocation;
    }
}