package alive.herbivore;

import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Deer extends Herbivore {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private int foodSaturation;
    private boolean isMoved;

    public Deer(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("DeerWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("DeerAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("DeerFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("DeerBreedFactor"));
        this.foodSaturation = Integer.parseInt(appProp.getProperty("DeerFoodSaturation"));
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
        foodSaturation -= 2;
        isDied();
    }

    @Override
    public void moveDirection() {
        int moveSpeed = ThreadLocalRandom.current().nextInt(maxAreaMove + 1);
        Location newLocation = Movement.getNewLocation(location, moveSpeed);

        if (newLocation != location &&
                newLocation.getPopulation().get("deerPopulation") < newLocation.getMaxPopulation().get("maxDeerPopulation")) {
            location.animalLeave(this, "deerPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "deerPopulation");
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
        int locationDeerPopulation = location.getPopulation().get("deerPopulation");
        if (locationDeerPopulation / breedFactor >= 2 &&
                locationDeerPopulation < location.getMaxPopulation().get("maxDeerPopulation")) {
            Deer newDeer = new Deer(location);
            newDeer.setIsMoved(true);
            location.animalArrive(newDeer, "deerPopulation");
        }
        foodSaturation -= 2;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "deerPopulation");
        }
    }
}
