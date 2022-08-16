package alive.herbivore;

import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Goat extends Herbivore {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private int foodSaturation;
    private boolean isMoved;

    public Goat(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("GoatWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("GoatAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("GoatFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("GoatBreedFactor"));
        this.foodSaturation = Integer.parseInt(appProp.getProperty("GoatFoodSaturation"));
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
                newLocation.getPopulation().get("goatPopulation") < newLocation.getMaxPopulation().get("maxGoatPopulation")) {
            location.animalLeave(this, "goatPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "goatPopulation");
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
        int locationGoatPopulation = location.getPopulation().get("goatPopulation");
        if (locationGoatPopulation / breedFactor >= 2 &&
                locationGoatPopulation < location.getMaxPopulation().get("maxGoatPopulation")) {
            Goat newGoat = new Goat(location);
            newGoat.setIsMoved(true);
            location.animalArrive(newGoat, "goatPopulation");
        }
        foodSaturation -= 3;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "goatPopulation");
        }
    }
}