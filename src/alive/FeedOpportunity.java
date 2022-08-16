package alive;

import alive.herbivore.*;
import alive.predator.*;

import java.util.concurrent.ThreadLocalRandom;

public class FeedOpportunity {

    public static boolean isEated(Animal eater, Animal victim) {
        boolean result = false;
        if (eater instanceof Wolf) {
            if ((victim instanceof Buffalo || victim instanceof Horse)
                    && getRandomChance() <= 10) {
                result = true;
            }
            if ((victim instanceof Boar || victim instanceof Deer)
                    && getRandomChance() <= 15) {
                result = true;
            }
            if (victim instanceof Duck && getRandomChance() <= 40) {
                result = true;
            }
            if ((victim instanceof Goat || victim instanceof Rabbit)
                    && getRandomChance() <= 60) {
                result = true;
            }
            if (victim instanceof Sheep && getRandomChance() <= 70) {
                result = true;
            }
            if (victim instanceof Mouse && getRandomChance() <= 80) {
                result = true;
            }
        }

        if (eater instanceof Boa) {
            if (victim instanceof Duck && getRandomChance() <= 10) {
                result = true;
            }
            if (victim instanceof Fox && getRandomChance() <= 15) {
                result = true;
            }
            if (victim instanceof Rabbit && getRandomChance() <= 20) {
                result = true;
            }
            if (victim instanceof Mouse && getRandomChance() <= 40) {
                result = true;
            }
        }

        if (eater instanceof Fox) {
            if (victim instanceof Caterpillar && getRandomChance() <= 40) {
                result = true;
            }
            if (victim instanceof Duck && getRandomChance() <= 60) {
                result = true;
            }
            if (victim instanceof Rabbit && getRandomChance() <= 70) {
                result = true;
            }
            if (victim instanceof Mouse && getRandomChance() <= 90) {
                result = true;
            }
        }

        if (eater instanceof Bear) {
            if (victim instanceof Duck && getRandomChance() <= 10) {
                result = true;
            }
            if (victim instanceof Buffalo && getRandomChance() <= 20) {
                result = true;
            }
            if (victim instanceof Horse && getRandomChance() <= 40) {
                result = true;
            }
            if (victim instanceof Boar && getRandomChance() <= 50) {
                result = true;
            }
            if ((victim instanceof Goat || victim instanceof Sheep)
                    && getRandomChance() <= 70) {
                result = true;
            }
            if ((victim instanceof Boa || victim instanceof Deer || victim instanceof Rabbit)
                    && getRandomChance() <= 80) {
                result = true;
            }
            if (victim instanceof Mouse && getRandomChance() <= 90) {
                result = true;
            }
        }

        if (eater instanceof Eagle) {
            if (victim instanceof Fox && getRandomChance() <= 10) {
                result = true;
            }
            if (victim instanceof Duck && getRandomChance() <= 80) {
                result = true;
            }
            if ((victim instanceof Rabbit || victim instanceof Mouse)
                    && getRandomChance() <= 90) {
                result = true;
            }
        }

        if ((eater instanceof Duck || eater instanceof Mouse)
                && victim instanceof Caterpillar) {
            if (getRandomChance() <= 90) {
                result = true;
            }
        }

        if (eater instanceof Boar) {
            if (victim instanceof Caterpillar && getRandomChance() <= 90) {
                result = true;
            }
            if (victim instanceof Mouse && getRandomChance() <= 50) {
                result = true;
            }
        }

        return result;
    }

    private static int getRandomChance() {
        return ThreadLocalRandom.current().nextInt(100 + 1);
    }
}