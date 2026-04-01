package Service;

import Algorithms.Treap;
import Model.ParkingFloor;
import Model.ParkingHouse;
import Model.VehicleInfo;

import java.util.List;
import java.util.Map;

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
        String log = treap.getOperationLog();

        if (!inserted) {
            return "Spot " + spot + " is already occupied.\n\nLast treap operation:\n" + log;
        }

        actionLog.append("Occupied floor ")
                .append(floor)
                .append(", spot ")
                .append(spot)
                .append(", vehicle: ")
                .append(info.licensePlate())
                .append("\n");

        return "Spot occupied successfully.\nTreap valid: "
                + treap.validateTreap()
                + "\n\nLast treap operation:\n"
                + log;
    }

    public String releaseSpot(int floor, int spot) {
        if (isInvalidFloor(floor) || isInvalidSpot(spot)) {
            return "Invalid floor or spot number.";
        }

        ParkingFloor parkingFloor = parkingHouse.getFloor(floor);
        Treap<Integer, VehicleInfo> treap = parkingFloor.getOccupiedTreap();

        boolean removed = treap.delete(spot);
        String log = treap.getOperationLog();

        if (!removed) {
            return "Spot " + spot + " is not occupied.\n\nLast treap operation:\n" + log;
        }

        actionLog.append("Released floor ")
                .append(floor)
                .append(", spot ")
                .append(spot)
                .append("\n");

        return "Spot released successfully.\nTreap valid: "
                + treap.validateTreap()
                + "\n\nLast treap operation:\n"
                + log;
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

        Treap<Integer, VehicleInfo> treap = parkingHouse.getFloor(floor).getOccupiedTreap();
        List<Map.Entry<Integer, VehicleInfo>> occupied = treap.getInOrder();

        if (occupied.isEmpty()) {
            return "No occupied spots on floor " + floor + ".";
        }

        StringBuilder result = new StringBuilder();
        result.append("Occupied spots on floor ").append(floor).append(":\n");

        for (Map.Entry<Integer, VehicleInfo> entry : occupied) {
            result.append("Spot ")
                    .append(entry.getKey())
                    .append(" -> ")
                    .append(entry.getValue())
                    .append("\n");
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

    public TreapHelper.SnapshotNode<Integer, VehicleInfo> getTreapSnapshotRoot(int floor) {
        if (isInvalidFloor(floor)) {
            return null;
        }

        Treap<Integer, VehicleInfo> treap = parkingHouse.getFloor(floor).getOccupiedTreap();
        return TreapHelper.buildSnapshot(treap);
    }

    public String getActionLog() {
        return actionLog.toString();
    }

    public int getFloorCount() {
        return parkingHouse.getFloorCount();
    }

    public int getSpotsPerFloor() {
        return parkingHouse.getSpotsPerFloor();
    }

    private boolean isInvalidFloor(int floor) {
        return floor < 1 || floor > parkingHouse.getFloorCount();
    }

    private boolean isInvalidSpot(int spot) {
        return spot < 1 || spot > parkingHouse.getSpotsPerFloor();
    }
}