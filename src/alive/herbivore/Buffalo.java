package alive.herbivore;

import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Buffalo extends Herbivore {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private int foodSaturation;
    private boolean isMoved;

    public Buffalo(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("BuffaloWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("BuffaloAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("BuffaloFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("BuffaloBreedFactor"));
        this.foodSaturation = Integer.parseInt(appProp.getProperty("BuffaloFoodSaturation"));
        this.isMoved = false;
    }

    @Override
    public boolean isMoved() {
        return isMoved;
    }

    @Override
    public void setIsMoved(boolean isMoved) {
        this.isMoved = isMoved;
    }

    @Override
    public void move() {
        moveDirection();
        setIsMoved(true);
        foodSaturation -= 5;
        isDied();
    }

    @Override
    public void moveDirection() {
        int moveSpeed = ThreadLocalRandom.current().nextInt(maxAreaMove + 1);
        Location newLocation = Movement.getNewLocation(location, moveSpeed);

        if (newLocation != location &&
                newLocation.getPopulation().get("buffaloPopulation") < newLocation.getMaxPopulation().get("maxBuffaloPopulation")) {
            location.animalLeave(this, "buffaloPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "buffaloPopulation");
        }
    }

    @Override
    public void eat(List<Plant> plant) {
        if (foodSaturation < maxSaturation) {
            if (!plant.isEmpty()) {
                plant.remove(0);
                foodSaturation += 1;
            } else {
                foodSaturation -= 1;
                isDied();
            }
        }
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationBuffaloPopulation = location.getPopulation().get("buffaloPopulation");
        if (locationBuffaloPopulation / breedFactor >= 2 &&
                locationBuffaloPopulation < location.getMaxPopulation().get("maxBuffaloPopulation")) {
            Buffalo newBuffalo = new Buffalo(location);
            newBuffalo.setIsMoved(true);
            location.animalArrive(newBuffalo, "buffaloPopulation");
        }
        foodSaturation -= 10;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "buffaloPopulation");
        }
    }
}
