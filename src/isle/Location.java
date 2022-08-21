package isle;

import alive.Animal;
import alive.Plant;
import alive.herbivore.*;
import alive.predator.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Location implements Runnable {
    private final Properties appProp;
    private final Location[][] islandMap;
    private final int[] locationCoordinates;
    private final Map<String, Integer> maxPopulation = new HashMap<>();
    private final Map<String, Integer> population = new HashMap<>();

    private final CopyOnWriteArrayList<Herbivore> herbivores = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Predator> predators = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Plant> plants = new CopyOnWriteArrayList<>();

    public Location(Properties appProp, Location[][] islandMap, int[] locationCoordinates) {
        this.appProp = appProp;
        this.islandMap = islandMap;
        this.locationCoordinates = locationCoordinates;
        this.maxPopulation.put("maxPlantPopulation", Integer.parseInt(appProp.getProperty("PlantPopulationMax")));
        this.maxPopulation.put("maxBearPopulation", Integer.parseInt(appProp.getProperty("BearPopulationMax")));
        this.maxPopulation.put("maxBoaPopulation", Integer.parseInt(appProp.getProperty("BoaPopulationMax")));
        this.maxPopulation.put("maxBoarPopulation", Integer.parseInt(appProp.getProperty("BoarPopulationMax")));
        this.maxPopulation.put("maxBuffaloPopulation", Integer.parseInt(appProp.getProperty("BuffaloPopulationMax")));
        this.maxPopulation.put("maxCaterpillarPopulation", Integer.parseInt(appProp.getProperty("CaterpillarPopulationMax")));
        this.maxPopulation.put("maxDeerPopulation", Integer.parseInt(appProp.getProperty("DeerPopulationMax")));
        this.maxPopulation.put("maxDuckPopulation", Integer.parseInt(appProp.getProperty("DuckPopulationMax")));
        this.maxPopulation.put("maxEaglePopulation", Integer.parseInt(appProp.getProperty("EaglePopulationMax")));
        this.maxPopulation.put("maxFoxPopulation", Integer.parseInt(appProp.getProperty("FoxPopulationMax")));
        this.maxPopulation.put("maxGoatPopulation", Integer.parseInt(appProp.getProperty("GoatPopulationMax")));
        this.maxPopulation.put("maxHorsePopulation", Integer.parseInt(appProp.getProperty("HorsePopulationMax")));
        this.maxPopulation.put("maxMousePopulation", Integer.parseInt(appProp.getProperty("MousePopulationMax")));
        this.maxPopulation.put("maxRabbitPopulation", Integer.parseInt(appProp.getProperty("RabbitPopulationMax")));
        this.maxPopulation.put("maxSheepPopulation", Integer.parseInt(appProp.getProperty("SheepPopulationMax")));
        this.maxPopulation.put("maxWolfPopulation", Integer.parseInt(appProp.getProperty("WolfPopulationMax")));
        initialize();
    }

    public Properties getAppProp() {
        return appProp;
    }

    public Location[][] getIslandMap() {
        return islandMap;
    }

    public int[] getLocationCoordinates() {
        return locationCoordinates;
    }

    public Map<String, Integer> getMaxPopulation() {
        return maxPopulation;
    }

    public Map<String, Integer> getPopulation() {
        return population;
    }

    public CopyOnWriteArrayList<Herbivore> getHerbivores() {
        return herbivores;
    }

    public CopyOnWriteArrayList<Predator> getPredators() {
        return predators;
    }

    public CopyOnWriteArrayList<Plant> getPlants() {
        return plants;
    }

    private synchronized void changePopulation(String populationType, int quantity) {
        population.computeIfPresent(populationType, (key, value) -> (value + quantity));
    }

    private void initialize() {
        growPlants();
        population.put("bearPopulation",
                initializePredators(Bear.class, maxPopulation.get("maxBearPopulation")));
        population.put("boaPopulation",
                initializePredators(Boa.class, maxPopulation.get("maxBoaPopulation")));
        population.put("boarPopulation",
                initializeHerbivores(Boar.class, maxPopulation.get("maxBoarPopulation")));
        population.put("buffaloPopulation",
                initializeHerbivores(Buffalo.class, maxPopulation.get("maxBuffaloPopulation")));
        population.put("caterpillarPopulation",
                initializeHerbivores(Caterpillar.class, maxPopulation.get("maxCaterpillarPopulation")));
        population.put("deerPopulation",
                initializeHerbivores(Deer.class, maxPopulation.get("maxDeerPopulation")));
        population.put("duckPopulation",
                initializeHerbivores(Duck.class, maxPopulation.get("maxDuckPopulation")));
        population.put("eaglePopulation",
                initializePredators(Eagle.class, maxPopulation.get("maxEaglePopulation")));
        population.put("foxPopulation",
                initializePredators(Fox.class, maxPopulation.get("maxFoxPopulation")));
        population.put("goatPopulation",
                initializeHerbivores(Goat.class, maxPopulation.get("maxGoatPopulation")));
        population.put("horsePopulation",
                initializeHerbivores(Horse.class, maxPopulation.get("maxHorsePopulation")));
        population.put("mousePopulation",
                initializeHerbivores(Mouse.class, maxPopulation.get("maxMousePopulation")));
        population.put("rabbitPopulation",
                initializeHerbivores(Rabbit.class, maxPopulation.get("maxRabbitPopulation")));
        population.put("sheepPopulation",
                initializeHerbivores(Sheep.class, maxPopulation.get("maxSheepPopulation")));
        population.put("wolfPopulation",
                initializePredators(Wolf.class, maxPopulation.get("maxWolfPopulation")));
    }

    private int initializeHerbivores(Class<?> herbivoreClass, int maxPopulation) {
        int population = ThreadLocalRandom.current().nextInt(maxPopulation + 1);
        for (int i = 0; i < population; i++) {
            try {
                herbivores.add((Herbivore) herbivoreClass
                        .getConstructor(Location.class)
                        .newInstance(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return population;
    }

    private int initializePredators(Class<?> predatorClass, int maxPopulation) {
        int population = ThreadLocalRandom.current().nextInt(maxPopulation + 1);
        for (int i = 0; i < population; i++) {
            try {
                predators.add((Predator) predatorClass
                        .getConstructor(Location.class)
                        .newInstance(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return population;
    }

    @Override
    public void run() {
        ThreadLocalRandom animalCase = ThreadLocalRandom.current();

        for (int i = 0; i < predators.size(); i++) {
            Predator predator = predators.get(i);

            if (!predator.isMoved()) {
                switch (animalCase.nextInt(3)) {
                    case 0 -> predator.breed();
                    case 1 -> predator.eat(herbivores);
                    case 2 -> predator.move();
                }
            } else {
                predator.setIsMoved(false);
            }
        }

        for (int i = 0; i < herbivores.size(); i++) {
            Herbivore herbivore = herbivores.get(i);

            if (!herbivore.isMoved()) {
                switch (animalCase.nextInt(3)) {
                    case 0 -> herbivore.breed();
                    case 1 -> herbivore.eat(plants);
                    case 2 -> herbivore.move();
                }
            } else {
                herbivore.setIsMoved(false);
            }
        }
        growPlants();
    }

    public synchronized void animalLeave(Animal animal, String populationType) {
        if (animal instanceof Predator) {
            predators.remove(animal);
        } else if (animal instanceof Herbivore) {
            herbivores.remove(animal);
        }
        changePopulation(populationType, -1);
    }

    public synchronized void animalArrive(Animal animal, String populationType) {
        if (animal instanceof Predator) {
            predators.add((Predator) animal);
        } else if (animal instanceof Herbivore) {
            herbivores.add((Herbivore) animal);
        }
        changePopulation(populationType, +1);
    }

    private void growPlants() {
        int random = ThreadLocalRandom.current().nextInt(maxPopulation.get("maxPlantPopulation") + 1);
        plants.clear();
        for (int i = 0; i < random; i++) {
            plants.add(new Plant());
        }
    }

    @Override
    public String toString() {
        return
                "  Хищников: " + predators.size() +
                        " (волков: " + population.get("wolfPopulation") +
                        ", удавов: " + population.get("boaPopulation") +
                        ", лис: " + population.get("foxPopulation") +
                        ", медведей: " + population.get("bearPopulation") +
                        ", орлов: " + population.get("eaglePopulation") + ")" +
                        "\n\t\t\t   Травоядных: " + herbivores.size() +
                        " (лошадей: " + population.get("horsePopulation") +
                        ", оленей: " + population.get("deerPopulation") +
                        ", кроликов: " + population.get("rabbitPopulation") +
                        ", мышей: " + population.get("mousePopulation") +
                        ", коз: " + population.get("goatPopulation") +
                        ", овец: " + population.get("sheepPopulation") +
                        ", кабанов: " + population.get("boarPopulation") +
                        ", буйволов: " + population.get("buffaloPopulation") +
                        ", уток: " + population.get("duckPopulation") +
                        ", гусениц: " + population.get("caterpillarPopulation") + ")" +
                        "\n\t\t\t   Растений: " + plants.size() +
                        "\n" +
                        "--------------" +
                        "\n";
    }
}