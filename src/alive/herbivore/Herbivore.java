package alive.herbivore;

import alive.Animal;
import alive.Plant;

import java.util.List;

public abstract class Herbivore extends Animal {

    public abstract void eat(List<Plant> plant);
}