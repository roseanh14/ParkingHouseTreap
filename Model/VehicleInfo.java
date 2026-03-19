package Model;

public class VehicleInfo {
    private final String licensePlate;
    private final String ownerName;
    private final String vehicleType;

    public VehicleInfo(String licensePlate, String ownerName, String vehicleType) {
        this.licensePlate = licensePlate;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    @Override
    public String toString() {
        return "Plate: " + licensePlate + ", Owner: " + ownerName + ", Type: " + vehicleType;
    }
}