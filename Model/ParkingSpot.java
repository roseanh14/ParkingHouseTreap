package Model;

import java.awt.Rectangle;

public class ParkingSpot {
    private final int number;
    private final Rectangle bounds;

    public ParkingSpot(int number, Rectangle bounds) {
        this.number = number;
        this.bounds = bounds;
    }

    public int getNumber() {
        return number;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}