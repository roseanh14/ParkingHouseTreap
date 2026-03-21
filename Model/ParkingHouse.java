package Model;

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