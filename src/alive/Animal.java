package alive;

public abstract class Animal {

    public abstract boolean isMoved();

    public abstract void setIsMoved(boolean isMoved);

    public abstract void move();

    public abstract void moveDirection();

    public abstract float getWeight();

    public abstract void breed();

    public abstract void isDied();
}
