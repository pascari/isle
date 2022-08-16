package alive.herbivore;

import alive.FeedOpportunity;
import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Boar extends Herbivore implements HerbivoreFeed {
    private Location location;
    private static int weight;
    private static int maxAreaMove;
    private static float maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Boar(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Integer.parseInt(appProp.getProperty("BoarWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("BoarAreaMoveSpeed"));
        maxSaturation = Float.parseFloat(appProp.getProperty("BoarFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("BoarBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("BoarFoodSaturation"));
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
                newLocation.getPopulation().get("boarPopulation") < newLocation.getMaxPopulation().get("maxBoarPopulation")) {
            location.animalLeave(this, "boarPopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "boarPopulation");
        }
    }

    @Override
    public void eatHerbivore(List<Herbivore> herbivores) {
        if (foodSaturation < maxSaturation) {
            for (Herbivore herbivore : herbivores) {
                if (herbivore instanceof Caterpillar &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "caterpillarPopulation");
                    foodSaturation += herbivore.getWeight();
                    return;
                } else if (herbivore instanceof Mouse &&
                        FeedOpportunity.isEated(this, herbivore)) {
                    location.animalLeave(herbivore, "mousePopulation");
                    foodSaturation += herbivore.getWeight();
                    return;
                }
            }
            foodSaturation -= 1;
            isDied();
        }
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void eat(List<Plant> plant) {
        if (foodSaturation < maxSaturation) {
            int boarChoosesFood = ThreadLocalRandom.current().nextInt(2);

            switch (boarChoosesFood) {
                case 0 -> {
                    if (!plant.isEmpty()) {
                        plant.remove(0);
                        foodSaturation += 1;
                    }
                }
                case 1 -> eatHerbivore(location.getHerbivores());
            }
        } else {
            foodSaturation -= 1;
            isDied();
        }
    }

    @Override
    public void breed() {
        int locationBoarPopulation = location.getPopulation().get("boarPopulation");
        if (locationBoarPopulation / breedFactor >= 2 &&
                locationBoarPopulation < location.getMaxPopulation().get("maxBoarPopulation")) {
            Boar newBoar = new Boar(location);
            newBoar.setIsMoved(true);
            location.animalArrive(newBoar, "boarPopulation");
        }
        foodSaturation -= 4;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "boarPopulation");
        }
    }
}
