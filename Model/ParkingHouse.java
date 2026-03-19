package Model;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class ParkingHouse {
    private final Map<Integer, ParkingFloor> floors = new HashMap<>();
    private static final int FLOOR_COUNT = 4;
    private static final int SPOTS_PER_FLOOR = 12;

    public ParkingHouse() {
        for (int i = 1; i <= FLOOR_COUNT; i++) {
            floors.put(i, new ParkingFloor(i));
        }
        initializeSpots();
    }

    private void initializeSpots() {
        for (int floor = 1; floor <= FLOOR_COUNT; floor++) {
            ParkingFloor parkingFloor = floors.get(floor);

            int x = 80;
            int y = 100;
            int width = 70;
            int height = 110;
            int gap = 18;

            for (int i = 1; i <= SPOTS_PER_FLOOR; i++) {
                parkingFloor.addSpot(new ParkingSpot(i, new Rectangle(x, y, width, height)));
                x += width + gap;

                if (i == 6) {
                    x = 80;
                    y += 180;
                }
            }
        }
    }

    public ParkingFloor getFloor(int floorNumber) {
        return floors.get(floorNumber);
    }

    public int getFloorCount() {
        return FLOOR_COUNT;
    }

    public int getSpotsPerFloor() {
        return SPOTS_PER_FLOOR;
    }
}