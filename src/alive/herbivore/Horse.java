package alive.herbivore;

import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Horse extends Herbivore {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private int foodSaturation;
    private boolean isMoved;

    public Horse(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("HorseWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("HorseAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("HorseFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("HorseBreedFactor"));
        this.foodSaturation = Integer.parseInt(appProp.getProperty("HorseFoodSaturation"));
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
        foodSaturation -= 3;
        isDied();
    }

    @Override
    public void moveDirection() {
        int moveSpeed = ThreadLocalRandom.current().nextInt(maxAreaMove + 1);
        Location newLocation = Movement.getNewLocation(location, moveSpeed);

        if (newLocation != location &&
                newLocation.getPopulation().get("horsePopulation") < newLocation.getMaxPopulation().get("maxHorsePopulation")) {
            location.animalLeave(this, "horsePopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "horsePopulation");
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
        int locationHorsePopulation = location.getPopulation().get("horsePopulation");
        if (locationHorsePopulation / breedFactor >= 2 &&
                locationHorsePopulation < location.getMaxPopulation().get("maxHorsePopulation")) {
            Horse newHorse = new Horse(location);
            newHorse.setIsMoved(true);
            location.animalArrive(newHorse, "horsePopulation");
        }
        foodSaturation -= 5;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "horsePopulation");
        }
    }
}