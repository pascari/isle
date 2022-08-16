package alive.predator;

import alive.Animal;
import alive.herbivore.Herbivore;

import java.util.List;

public abstract class Predator extends Animal {

    public abstract void eat(List<Herbivore> herbivores);
}