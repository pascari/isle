package alive.herbivore;

import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Rabbit extends Herbivore {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static float maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Rabbit(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("RabbitWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("RabbitAreaMoveSpeed"));
        maxSaturation = Float.parseFloat(appProp.getProperty("RabbitFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("RabbitBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("RabbitFoodSaturation"));
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
        foodSaturation -= 0.225;
        isDied();
    }

    @Override
    public void moveDirection() {
        int moveSpeed = ThreadLocalRandom.current().nextInt(maxAreaMove + 1);
        Location newLocation = Movement.getNewLocation(location, moveSpeed);

        if (newLocation != location &&
                newLocation.getPopulation().get("rabbitPopulation") < newLocation.getMaxPopulation().get("maxRabbitPopulation")) {
            location.animalLeave(this, "rabbitPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "rabbitPopulation");
        }
    }

    @Override
    public void eat(List<Plant> plant) {
        if (foodSaturation < maxSaturation) {
            if (!plant.isEmpty()) {
                plant.remove(0);
                foodSaturation = maxSaturation;
            } else {
                foodSaturation -= 0.225;
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
        int locationRabbitPopulation = location.getPopulation().get("rabbitPopulation");
        if (locationRabbitPopulation / breedFactor >= 2 &&
                locationRabbitPopulation < location.getMaxPopulation().get("maxRabbitPopulation")) {
            Rabbit newRabbit = new Rabbit(location);
            newRabbit.setIsMoved(true);
            location.animalArrive(newRabbit, "rabbitPopulation");
        }
        foodSaturation -= 0.23;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "rabbitPopulation");
        }
    }
}