package alive.herbivore;

import alive.FeedOpportunity;
import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Duck extends Herbivore implements HerbivoreFeed {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static float maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Duck(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("DuckWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("DuckAreaMoveSpeed"));
        maxSaturation = Float.parseFloat(appProp.getProperty("DuckFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("DuckBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("DuckFoodSaturation"));
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
        foodSaturation -= 0.05f;
        isDied();
    }

    @Override
    public void moveDirection() {
        int moveSpeed = ThreadLocalRandom.current().nextInt(maxAreaMove + 1);
        Location newLocation = Movement.getNewLocation(location, moveSpeed);

        if (newLocation != location &&
                newLocation.getPopulation().get("duckPopulation") < newLocation.getMaxPopulation().get("maxDuckPopulation")) {
            location.animalLeave(this, "duckPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "duckPopulation");
        }
    }

    @Override
    public void eat(List<Plant> plant) {
        if (foodSaturation < maxSaturation) {
            int duckChoosesFood = ThreadLocalRandom.current().nextInt(2);

            switch (duckChoosesFood) {
                case 0 -> {
                    if (!plant.isEmpty()) {
                        plant.remove(0);
                        foodSaturation = maxSaturation;
                    }
                }
                case 1 -> eatHerbivore(location.getHerbivores());
            }
        } else {
            foodSaturation -= 0.05f;
            isDied();
        }
    }

    @Override
    public void eatHerbivore(List<Herbivore> herbivores) {
        for (Herbivore herbivore : herbivores) {
            if (herbivore instanceof Caterpillar &&
                    FeedOpportunity.isEated(this, herbivore)) {
                location.animalLeave(herbivore, "caterpillarPopulation");
                foodSaturation += herbivore.getWeight();
                return;
            }
        }
        foodSaturation -= 0.05f;
        isDied();
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationDuckPopulation = location.getPopulation().get("duckPopulation");
        if (locationDuckPopulation / breedFactor >= 2 &&
                locationDuckPopulation < location.getMaxPopulation().get("maxDuckPopulation")) {
            Duck newDuck = new Duck(location);
            newDuck.setIsMoved(true);
            location.animalArrive(newDuck, "duckPopulation");
        }
        foodSaturation -= 0.05f;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "duckPopulation");
        }
    }
}