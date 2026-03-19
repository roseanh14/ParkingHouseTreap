package Model;

import Algorithms.Treap;

import java.util.ArrayList;
import java.util.List;

public class ParkingFloor {
    private final int floorNumber;
    private final Treap<Integer, VehicleInfo> occupiedTreap;
    private final List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.occupiedTreap = new Treap<>();
        this.spots = new ArrayList<>();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Treap<Integer, VehicleInfo> getOccupiedTreap() {
        return occupiedTreap;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }
}