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

public class Boa extends Predator implements PredatorFeed {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static int maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Boa(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("BoaWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("BoaAreaMoveSpeed"));
        maxSaturation = Integer.parseInt(appProp.getProperty("BoaFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("BoaBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("BoaFoodSaturation"));
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
        foodSaturation -= 0.75f;
        isDied();
    }

    @Override
    public void moveDirection() {
        int moveSpeed = ThreadLocalRandom.current().nextInt(maxAreaMove + 1);
        Location newLocation = Movement.getNewLocation(location, moveSpeed);

        if (newLocation != location &&
                newLocation.getPopulation().get("boaPopulation") < newLocation.getMaxPopulation().get("maxBoaPopulation")) {
            location.animalLeave(this, "boaPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "boaPopulation");
        }
    }

    @Override
    public void eat(List<Herbivore> herbivores) {
        if (foodSaturation < maxSaturation) {
            int boaChoosesFood = ThreadLocalRandom.current().nextInt(2);

            switch (boaChoosesFood) {
                case 0 -> {
                    for (Herbivore herbivore : herbivores) {
                        if (herbivore instanceof Duck &&
                                FeedOpportunity.isEated(this, herbivore)) {
                            location.animalLeave(herbivore, "duckPopulation");
                            foodSaturation += herbivore.getWeight();
                            return;
                        } else if (herbivore instanceof Mouse &&
                                FeedOpportunity.isEated(this, herbivore)) {
                            location.animalLeave(herbivore, "mousePopulation");
                            foodSaturation += herbivore.getWeight();
                            return;
                        } else if (herbivore instanceof Rabbit &&
                                FeedOpportunity.isEated(this, herbivore)) {
                            location.animalLeave(herbivore, "rabbitPopulation");
                            if ((foodSaturation += herbivore.getWeight()) > maxSaturation) {
                                foodSaturation = maxSaturation;
                            } else {
                                foodSaturation += herbivore.getWeight();
                            }
                            return;
                        }
                    }
                }
                case 1 -> eatPredator(location.getPredators());
            }
        } else {
            foodSaturation -= 0.75f;
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
        foodSaturation -= 0.75f;
        isDied();
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationBoaPopulation = location.getPopulation().get("boaPopulation");
        if (locationBoaPopulation / breedFactor >= 2 &&
                locationBoaPopulation < location.getMaxPopulation().get("maxBoaPopulation")) {
            Boa newBoa = new Boa(location);
            newBoa.setIsMoved(true);
            location.animalArrive(newBoa, "boaPopulation");
        }
        foodSaturation -= 1;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "boaPopulation");
        }
    }
}