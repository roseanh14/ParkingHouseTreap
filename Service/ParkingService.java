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

        treap.clearRotationLog();

        boolean inserted = treap.insert(spot, info);
        if (!inserted) {
            String log = treap.getRotationLog();
            if (log.isBlank()) {
                return "Spot " + spot + " is already occupied.";
            }
            return "Spot " + spot + " is already occupied.\n\nTreap log:\n" + log;
        }

        actionLog.append("Occupied floor ")
                .append(floor)
                .append(", spot ")
                .append(spot)
                .append(", vehicle: ")
                .append(info.licensePlate())
                .append("\n");

        StringBuilder result = new StringBuilder();
        result.append("Spot occupied successfully.\n");
        result.append("Treap valid: ").append(treap.validateTreap());

        String log = treap.getRotationLog();
        if (!log.isBlank()) {
            result.append("\n\nTreap log:\n").append(log);
        }

        return result.toString();
    }

    public String releaseSpot(int floor, int spot) {
        if (isInvalidFloor(floor) || isInvalidSpot(spot)) {
            return "Invalid floor or spot number.";
        }

        ParkingFloor parkingFloor = parkingHouse.getFloor(floor);
        Treap<Integer, VehicleInfo> treap = parkingFloor.getOccupiedTreap();

        treap.clearRotationLog();

        boolean removed = treap.delete(spot);
        if (!removed) {
            String log = treap.getRotationLog();
            if (log.isBlank()) {
                return "Spot " + spot + " is not occupied.";
            }
            return "Spot " + spot + " is not occupied.\n\nTreap log:\n" + log;
        }

        actionLog.append("Released floor ")
                .append(floor)
                .append(", spot ")
                .append(spot)
                .append("\n");

        StringBuilder result = new StringBuilder();
        result.append("Spot released successfully.\n");
        result.append("Treap valid: ").append(treap.validateTreap());

        String log = treap.getRotationLog();
        if (!log.isBlank()) {
            result.append("\n\nTreap log:\n").append(log);
        }

        return result.toString();
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

        int leftBoundary = spot;
        int rightBoundary = spot;

        Integer currentLeft = predecessor;
        while (currentLeft != null && currentLeft == leftBoundary - 1) {
            leftBoundary = currentLeft;
            currentLeft = treap.predecessor(leftBoundary);
        }

        Integer currentRight = successor;
        while (currentRight != null && currentRight == rightBoundary + 1) {
            rightBoundary = currentRight;
            currentRight = treap.successor(rightBoundary);
        }

        int leftCandidate = leftBoundary - 1;
        int rightCandidate = rightBoundary + 1;

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

    public String printTreap(int floor) {
        if (isInvalidFloor(floor)) {
            return "Invalid floor.";
        }

        return parkingHouse.getFloor(floor).getOccupiedTreap().printTreap();
    }

    public Treap.ViewNode<Integer, VehicleInfo> getTreapViewRoot(int floor) {
        if (isInvalidFloor(floor)) {
            return null;
        }

        return parkingHouse.getFloor(floor).getOccupiedTreap().getViewRoot();
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