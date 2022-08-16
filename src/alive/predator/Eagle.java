package alive.predator;

import alive.FeedOpportunity;
import alive.Movement;
import alive.herbivore.Duck;
import alive.herbivore.Herbivore;
import alive.herbivore.Mouse;
import alive.herbivore.Rabbit;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Eagle extends Predator implements PredatorFeed {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Eagle(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("EagleWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("EagleAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("EagleFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("EagleBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("EagleFoodSaturation"));
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
                newLocation.getPopulation().get("eaglePopulation") < newLocation.getMaxPopulation().get("maxEaglePopulation")) {
            location.animalLeave(this, "eaglePopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "eaglePopulation");
        }
    }

    @Override
    public void eat(List<Herbivore> herbivores) {
        if (foodSaturation < maxSaturation) {
            int eagleChoosesFood = ThreadLocalRandom.current().nextInt(2);

            switch (eagleChoosesFood) {
                case 0 -> {
                    for (Herbivore herbivore : herbivores) {
                        if (herbivore instanceof Duck &&
                                FeedOpportunity.isEated(this, herbivore)) {
                            location.animalLeave(herbivore, "duckPopulation");
                            foodSaturation = maxSaturation;
                            return;
                        } else if (herbivore instanceof Mouse &&
                                FeedOpportunity.isEated(this, herbivore)) {
                            location.animalLeave(herbivore, "mousePopulation");
                            foodSaturation += herbivore.getWeight();
                            return;
                        } else if (herbivore instanceof Rabbit &&
                                FeedOpportunity.isEated(this, herbivore)) {
                            location.animalLeave(herbivore, "rabbitPopulation");
                            foodSaturation = maxSaturation;
                            return;
                        }
                    }
                }
                case 1 -> eatPredator(location.getPredators());
            }
        } else {
            foodSaturation -= 0.25f;
            isDied();
        }
    }

    @Override
    public void eatPredator(List<Predator> predators) {
        for (Predator predator : predators) {
            if (predator instanceof Fox &&
                    FeedOpportunity.isEated(this, predator)) {
                location.animalLeave(predator, "foxPopulation");
                foodSaturation = maxSaturation;
                return;
            }
        }
        foodSaturation -= 0.25f;
        isDied();
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationEaglePopulation = location.getPopulation().get("eaglePopulation");
        if (locationEaglePopulation / breedFactor >= 2 &&
                locationEaglePopulation < location.getMaxPopulation().get("maxEaglePopulation")) {
            Eagle newEagle = new Eagle(location);
            newEagle.setIsMoved(true);
            location.animalArrive(newEagle, "eaglePopulation");
        }
        foodSaturation -= 0.5f;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "eaglePopulation");
        }
    }
}