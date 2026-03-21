package Service;

import Algorithms.Treap;
import Model.ParkingFloor;
import Model.ParkingHouse;
import Model.VehicleInfo;

import java.util.List;

public class ParkingService {

    private final ParkingHouse parkingHouse;
    private final StringBuilder actionLog = new StringBuilder();

    public ParkingService(ParkingHouse parkingHouse) {
        this.parkingHouse = parkingHouse;
    }

    public String occupySpot(int floor, int spot, VehicleInfo info) {
        if (isInvalidFloor(floor) || isInvalidSpot(spot)) {
            return "Invalid floor or spot number.";
        }

        ParkingFloor parkingFloor = parkingHouse.getFloor(floor);
        Treap<Integer, VehicleInfo> treap = parkingFloor.getOccupiedTreap();

        boolean inserted = treap.insert(spot, info);
        if (!inserted) {
            return "Spot " + spot + " is already occupied.";
        }

        actionLog.append("Occupied floor ")
                .append(floor)
                .append(", spot ")
                .append(spot)
                .append(", vehicle: ")
                .append(info.licensePlate())
                .append("\n");

        return "Spot occupied successfully. Treap valid: " + treap.validateTreap();
    }

    public String releaseSpot(int floor, int spot) {
        if (isInvalidFloor(floor) || isInvalidSpot(spot)) {
            return "Invalid floor or spot number.";
        }

        ParkingFloor parkingFloor = parkingHouse.getFloor(floor);
        Treap<Integer, VehicleInfo> treap = parkingFloor.getOccupiedTreap();

        boolean removed = treap.delete(spot);
        if (!removed) {
            return "Spot " + spot + " is not occupied.";
        }

        actionLog.append("Released floor ")
                .append(floor)
                .append(", spot ")
                .append(spot)
                .append("\n");

        return "Spot released successfully. Treap valid: " + treap.validateTreap();
    }

    public boolean isOccupied(int floor, int spot) {
        if (isInvalidFloor(floor) || isInvalidSpot(spot)) {
            return false;
        }

        return parkingHouse.getFloor(floor).getOccupiedTreap().contains(spot);
    }

    public VehicleInfo getSpotInfo(int floor, int spot) {
        if (isInvalidFloor(floor) || isInvalidSpot(spot)) {
            return null;
        }

        return parkingHouse.getFloor(floor).getOccupiedTreap().search(spot);
    }

    public String listOccupiedSpots(int floor) {
        if (isInvalidFloor(floor)) {
            return "Invalid floor.";
        }

        List<String> occupied = parkingHouse.getFloor(floor).getOccupiedTreap().inorder();

        if (occupied.isEmpty()) {
            return "No occupied spots on floor " + floor + ".";
        }

        StringBuilder result = new StringBuilder();
        result.append("Occupied spots on floor ").append(floor).append(":\n");

        for (String item : occupied) {
            result.append(item).append("\n");
        }

        return result.toString();
    }

    public int findNearestFreeSpot(int floor, int spot) {
        if (isInvalidFloor(floor) || isInvalidSpot(spot)) {
            return -1;
        }

        ParkingFloor parkingFloor = parkingHouse.getFloor(floor);
        Treap<Integer, VehicleInfo> treap = parkingFloor.getOccupiedTreap();
        int maxSpot = parkingHouse.getSpotsPerFloor();

        if (!treap.contains(spot)) {
            return spot;
        }

        Integer predecessor = treap.predecessor(spot);
        Integer successor = treap.successor(spot);

        int leftCandidate = (predecessor == null) ? spot - 1 : predecessor - 1;
        int rightCandidate = (successor == null) ? spot + 1 : successor + 1;

        while (leftCandidate >= 1 && treap.contains(leftCandidate)) {
            leftCandidate--;
        }

        while (rightCandidate <= maxSpot && treap.contains(rightCandidate)) {
            rightCandidate++;
        }

        if (leftCandidate < 1 && rightCandidate > maxSpot) {
            return -1;
        }

        if (leftCandidate < 1) {
            return rightCandidate;
        }

        if (rightCandidate > maxSpot) {
            return leftCandidate;
        }

        int leftDistance = Math.abs(spot - leftCandidate);
        int rightDistance = Math.abs(rightCandidate - spot);

        return (leftDistance <= rightDistance) ? leftCandidate : rightCandidate;
    }

    public String validateTreap(int floor) {
        if (isInvalidFloor(floor)) {
            return "Invalid floor.";
        }

        boolean valid = parkingHouse.getFloor(floor).getOccupiedTreap().validateTreap();
        return "Treap validation for floor " + floor + ": " + valid;
    }

    public String getActionLog() {
        return actionLog.toString();
    }

    private boolean isInvalidFloor(int floor) {
        return floor < 1 || floor > parkingHouse.getFloorCount();
    }

    private boolean isInvalidSpot(int spot) {
        return spot < 1 || spot > parkingHouse.getSpotsPerFloor();
    }
}