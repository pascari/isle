package alive.predator;

import alive.FeedOpportunity;
import alive.Movement;
import alive.herbivore.*;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Wolf extends Predator {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Wolf(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("WolfWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("WolfAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("WolfFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("WolfBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("WolfFoodSaturation"));
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
                newLocation.getPopulation().get("wolfPopulation") < newLocation.getMaxPopulation().get("maxWolfPopulation")) {
            location.animalLeave(this, "wolfPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "wolfPopulation");
        }
    }

    @Override
    public void eat(List<Herbivore> herbivores) {
        if (foodSaturation < maxSaturation) {
            for (Herbivore herbivore : herbivores) {
                if (herbivore instanceof Boar &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "boarPopulation");
                    foodSaturation = maxSaturation;
                    return;
                } else if (herbivore instanceof Buffalo &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "buffaloPopulation");
                    foodSaturation = maxSaturation;
                    return;
                } else if (herbivore instanceof Deer &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "deerPopulation");
                    foodSaturation = maxSaturation;
                    return;
                } else if (herbivore instanceof Duck &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "duckPopulation");
                    foodSaturation += herbivore.getWeight();
                    return;
                }  else if (herbivore instanceof Goat &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "goatPopulation");
                    foodSaturation = maxSaturation;
                    return;
                } else if (herbivore instanceof Horse &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "horsePopulation");
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
                    foodSaturation += herbivore.getWeight();
                    return;
                } else if (herbivore instanceof Sheep &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "sheepPopulation");
                    foodSaturation = maxSaturation;
                    return;
                }
            }
            foodSaturation -= 2;
            isDied();
        }
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationWolfPopulation = location.getPopulation().get("wolfPopulation");
        if (locationWolfPopulation / breedFactor >= 2 &&
                locationWolfPopulation < location.getMaxPopulation().get("maxWolfPopulation")) {
            Wolf newWolf = new Wolf(location);
            newWolf.setIsMoved(true);
            location.animalArrive(newWolf, "wolfPopulation");
        }
        foodSaturation -= 2;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "wolfPopulation");
        }
    }
}