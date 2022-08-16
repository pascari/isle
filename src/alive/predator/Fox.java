package alive.predator;

import alive.FeedOpportunity;
import alive.Movement;
import alive.herbivore.*;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Fox extends Predator {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Fox(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("FoxWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("FoxAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("FoxFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("FoxBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("FoxFoodSaturation"));
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
        foodSaturation -= 0.5f;
        isDied();
    }

    @Override
    public void moveDirection() {
        int moveSpeed = ThreadLocalRandom.current().nextInt(maxAreaMove + 1);
        Location newLocation = Movement.getNewLocation(location, moveSpeed);

        if (newLocation != location &&
                newLocation.getPopulation().get("foxPopulation") < newLocation.getMaxPopulation().get("maxFoxPopulation")) {
            location.animalLeave(this, "foxPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "foxPopulation");
        }
    }

    @Override
    public void eat(List<Herbivore> herbivores) {
        if (foodSaturation < maxSaturation) {
            for (Herbivore herbivore : herbivores) {
                if (herbivore instanceof Caterpillar &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "caterpillarPopulation");
                    foodSaturation += herbivore.getWeight();
                    return;
                } else if (herbivore instanceof Duck &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "duckPopulation");
                    if ((foodSaturation += herbivore.getWeight()) > maxSaturation) {
                        foodSaturation = maxSaturation;
                    } else {
                        foodSaturation += herbivore.getWeight();
                    }
                    return;
                } else if (herbivore instanceof Mouse &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "mousePopulation");
                    foodSaturation += herbivore.getWeight();
                    return;
                } else if (herbivore instanceof Rabbit &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "rabbitPopulation");
                    foodSaturation += maxSaturation;
                    return;
                }
            }
            foodSaturation -= 0.5f;
            isDied();
        }
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationFoxPopulation = location.getPopulation().get("foxPopulation");
        if (locationFoxPopulation / breedFactor >= 2 &&
                locationFoxPopulation < location.getMaxPopulation().get("maxFoxPopulation")) {
            Fox newFox = new Fox(location);
            newFox.setIsMoved(true);
            location.animalArrive(newFox, "foxPopulation");
        }
        foodSaturation -= 0.5f;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "foxPopulation");
        }
    }
}