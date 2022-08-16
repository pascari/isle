package alive.herbivore;

import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Sheep extends Herbivore {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private int foodSaturation;
    private boolean isMoved;

    public Sheep(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("SheepWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("SheepAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("SheepFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("SheepBreedFactor"));
        this.foodSaturation = Integer.parseInt(appProp.getProperty("SheepFoodSaturation"));
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
                newLocation.getPopulation().get("sheepPopulation") < newLocation.getMaxPopulation().get("maxSheepPopulation")) {
            location.animalLeave(this, "sheepPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "sheepPopulation");
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
        int locationSheepPopulation = location.getPopulation().get("sheepPopulation");
        if (locationSheepPopulation / breedFactor >= 2 &&
                locationSheepPopulation < location.getMaxPopulation().get("maxSheepPopulation")) {
            Sheep newSheep = new Sheep(location);
            newSheep.setIsMoved(true);
            location.animalArrive(newSheep, "sheepPopulation");
        }
        foodSaturation -= 4;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "sheepPopulation");
        }
    }
}