package Model;

import Algorithms.Treap;

public class ParkingFloor {

    private final int floorNumber;
    private final Treap<Integer, VehicleInfo> occupiedTreap;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.occupiedTreap = new Treap<>();
    }

    public Treap<Integer, VehicleInfo> getOccupiedTreap() {
        return occupiedTreap;
    }

    @Override
    public String toString() {
        return "Floor " + floorNumber;
    }
}