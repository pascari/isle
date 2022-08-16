package alive.predator;

import alive.FeedOpportunity;
import alive.Movement;
import alive.herbivore.*;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Bear extends Predator implements PredatorFeed {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Bear(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("BearWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("BearAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("BearFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("BearBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("BearFoodSaturation"));
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
                newLocation.getPopulation().get("bearPopulation") < newLocation.getMaxPopulation().get("maxBearPopulation")) {
            location.animalLeave(this, "bearPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "bearPopulation");
        }
    }

    @Override
    public void eat(List<Herbivore> herbivores) {
        if (foodSaturation < maxSaturation) {
            int bearChoosesFood = ThreadLocalRandom.current().nextInt(2);

            switch (bearChoosesFood) {
                case 0 -> {
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
                            recalculateSaturation(herbivore.getWeight());
                            return;
                        }  else if (herbivore instanceof Goat &&
                                FeedOpportunity.isEated(this, herbivore)) {
                            location.animalLeave(herbivore, "goatPopulation");
                            recalculateSaturation(herbivore.getWeight());
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
                            recalculateSaturation(herbivore.getWeight());
                            return;
                        } else if (herbivore instanceof Sheep &&
                                FeedOpportunity.isEated(this, herbivore)) {
                            location.animalLeave(herbivore, "sheepPopulation");
                            recalculateSaturation(herbivore.getWeight());
                            return;
                        }
                    }
                }
                case 1 -> eatPredator(location.getPredators());
            }
        } else {
            foodSaturation -= 5;
            isDied();
        }
    }

    @Override
    public void eatPredator(List<Predator> predators) {
        for (Predator predator : predators) {
            if (predator instanceof Boa &&
                    FeedOpportunity.isEated(this, predator)) {
                location.animalLeave(predator, "boaPopulation");
                recalculateSaturation(predator.getWeight());
                return;
            }
        }
        foodSaturation -= 5;
        isDied();
    }

    private void recalculateSaturation(float victimWeight) {
        if ((foodSaturation += victimWeight) > maxSaturation) {
            foodSaturation = maxSaturation;
        } else {
            foodSaturation += victimWeight;
        }
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationBearPopulation = location.getPopulation().get("bearPopulation");
        if (locationBearPopulation / breedFactor >= 2 &&
                locationBearPopulation < location.getMaxPopulation().get("maxBearPopulation")) {
            Bear newBear = new Bear(location);
            newBear.setIsMoved(true);
            location.animalArrive(newBear, "bearPopulation");
        }
        foodSaturation -= 10;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "bearPopulation");
        }
    }
}