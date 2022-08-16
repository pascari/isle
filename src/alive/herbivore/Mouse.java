package alive.herbivore;

import alive.FeedOpportunity;
import alive.Movement;
import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Mouse extends Herbivore implements HerbivoreFeed {
    private Location location;
    private static float weight;
    private static int maxAreaMove;
    private static float maxSaturation;
    private static int breedFactor;
    private float foodSaturation;
    private boolean isMoved;

    public Mouse(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Float.parseFloat(appProp.getProperty("MouseWeight"));
        maxAreaMove = Integer.parseInt(appProp.getProperty("MouseAreaMoveSpeed"));
        maxSaturation = Float.parseFloat(appProp.getProperty("MouseFoodSaturationMax"));
        breedFactor = Integer.parseInt(appProp.getProperty("MouseBreedFactor"));
        this.foodSaturation = Float.parseFloat(appProp.getProperty("MouseFoodSaturation"));
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
        foodSaturation -= 0.005f;
        isDied();
    }

    @Override
    public void moveDirection() {
        int moveSpeed = ThreadLocalRandom.current().nextInt(maxAreaMove + 1);
        Location newLocation = Movement.getNewLocation(location, moveSpeed);

        if (newLocation != location &&
                newLocation.getPopulation().get("mousePopulation") < newLocation.getMaxPopulation().get("maxMousePopulation")) {
            location.animalLeave(this, "mousePopulation");
            this.location = newLocation;
            newLocation.animalArrive(this, "mousePopulation");
        }
    }

    @Override
    public void eat(List<Plant> plant) {
        if (foodSaturation < maxSaturation) {
            int mouseChoosesFood = ThreadLocalRandom.current().nextInt(2);

            switch (mouseChoosesFood) {
                case 0 -> {
                    if (!plant.isEmpty()) {
                        plant.remove(0);
                        foodSaturation = maxSaturation;
                    }
                }
                case 1 -> eatHerbivore(location.getHerbivores());
            }
        } else {
            foodSaturation -= 0.005f;
            isDied();
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
                }
            }
            foodSaturation -= 0.005f;
            isDied();
        }
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationMousePopulation = location.getPopulation().get("mousePopulation");
        if (locationMousePopulation / breedFactor >= 2 &&
                locationMousePopulation < location.getMaxPopulation().get("maxMousePopulation")) {
            Mouse newMouse = new Mouse(location);
            newMouse.setIsMoved(true);
            location.animalArrive(newMouse, "mousePopulation");
        }
        foodSaturation -= 0.005f;
        isDied();
    }

    @Override
    public void isDied() {
        if (foodSaturation < 0) {
            location.animalLeave(this, "mousePopulation");
        }
    }
}