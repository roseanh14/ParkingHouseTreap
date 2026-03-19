package Service;

import Model.ParkingFloor;
import Model.ParkingHouse;
import Algorithms.Treap;
import Model.VehicleInfo;

import java.util.List;

public class ParkingService {
    private final ParkingHouse parkingHouse;
    private final StringBuilder actionLog = new StringBuilder();

    public ParkingService(ParkingHouse parkingHouse) {
        this.parkingHouse = parkingHouse;
    }

    public ParkingHouse getParkingHouse() {
        return parkingHouse;
    }

    public String occupySpot(int floor, int spot, VehicleInfo info) {
        if (!isValidFloor(floor) || !isValidSpot(spot)) {
            return "Invalid floor or spot number.";
        }

        ParkingFloor parkingFloor = parkingHouse.getFloor(floor);
        Treap<Integer, VehicleInfo> treap = parkingFloor.getOccupiedTreap();

        if (treap.contains(spot)) {
            return "Spot " + spot + " is already occupied.";
        }

        treap.insert(spot, info);
        actionLog.append("Occupied floor ").append(floor).append(", spot ").append(spot).append("\n");
        return "Spot occupied successfully. Treap valid: " + treap.validateTreap();
    }

    public String releaseSpot(int floor, int spot) {
        if (!isValidFloor(floor) || !isValidSpot(spot)) {
            return "Invalid floor or spot number.";
        }

        ParkingFloor parkingFloor = parkingHouse.getFloor(floor);
        Treap<Integer, VehicleInfo> treap = parkingFloor.getOccupiedTreap();

        if (!treap.contains(spot)) {
            return "Spot " + spot + " is not occupied.";
        }

        treap.delete(spot);
        actionLog.append("Released floor ").append(floor).append(", spot ").append(spot).append("\n");
        return "Spot released successfully. Treap valid: " + treap.validateTreap();
    }

    public boolean isOccupied(int floor, int spot) {
        return parkingHouse.getFloor(floor).getOccupiedTreap().contains(spot);
    }

    public VehicleInfo getSpotInfo(int floor, int spot) {
        return parkingHouse.getFloor(floor).getOccupiedTreap().search(spot);
    }

    public String listOccupiedSpots(int floor) {
        if (!isValidFloor(floor)) {
            return "Invalid floor.";
        }

        List<String> list = parkingHouse.getFloor(floor).getOccupiedTreap().inorder();
        if (list.isEmpty()) {
            return "No occupied spots on floor " + floor + ".";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Occupied spots on floor ").append(floor).append(":\n");
        for (String item : list) {
            sb.append(item).append("\n");
        }
        return sb.toString();
    }

    public int findNearestFreeSpot(int floor, int requestedSpot) {
        if (!isValidFloor(floor) || !isValidSpot(requestedSpot)) {
            return -1;
        }

        Treap<Integer, VehicleInfo> treap = parkingHouse.getFloor(floor).getOccupiedTreap();
        int maxSpot = parkingHouse.getSpotsPerFloor();

        if (!treap.contains(requestedSpot)) {
            return requestedSpot;
        }

        int left = requestedSpot - 1;
        int right = requestedSpot + 1;

        while (left >= 1 || right <= maxSpot) {
            if (left >= 1 && !treap.contains(left)) {
                return left;
            }
            if (right <= maxSpot && !treap.contains(right)) {
                return right;
            }
            left--;
            right++;
        }

        return -1;
    }

    public String validateTreap(int floor) {
        if (!isValidFloor(floor)) {
            return "Invalid floor.";
        }
        return "Treap validation for floor " + floor + ": "
                + parkingHouse.getFloor(floor).getOccupiedTreap().validateTreap();
    }

    public String getActionLog() {
        return actionLog.toString();
    }

    private boolean isValidFloor(int floor) {
        return floor >= 1 && floor <= parkingHouse.getFloorCount();
    }

    private boolean isValidSpot(int spot) {
        return spot >= 1 && spot <= parkingHouse.getSpotsPerFloor();
    }
}