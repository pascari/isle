package alive.herbivore;

import alive.Plant;
import isle.Location;

import java.util.List;
import java.util.Properties;

public class Caterpillar extends Herbivore {
    private final Location location;
    private static float weight;
    private static int breedFactor;
    private boolean isMoved;

    public Caterpillar(Location location) {
        this.location = location;
        Properties appProp = location.getAppProp();
        weight = Float.parseFloat(appProp.getProperty("CaterpillarWeight"));
        breedFactor = Integer.parseInt(appProp.getProperty("CaterpillarBreedFactor"));
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
        isDied();
    }

    @Override
    public void moveDirection() {
    }

    @Override
    public void eat(List<Plant> plant) {
        if (!plant.isEmpty()) {
            plant.remove(0);
        } else {
            isDied();
        }
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void breed() {
        int locationCaterpillarPopulation = location.getPopulation().get("caterpillarPopulation");
        if (locationCaterpillarPopulation / breedFactor >= 2 &&
                locationCaterpillarPopulation < location.getMaxPopulation().get("maxCaterpillarPopulation")) {
            Caterpillar newCaterpillar = new Caterpillar(location);
            newCaterpillar.setIsMoved(true);
            location.animalArrive(newCaterpillar, "caterpillarPopulation");
        }
    }

    @Override
    public void isDied() {
        location.animalLeave(this, "caterpillarPopulation");
    }
}
