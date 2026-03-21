package Model;

public record VehicleInfo(String licensePlate, String ownerName, String vehicleType) {

    @Override
    public String toString() {
        return "Plate: " + licensePlate + ", Owner: " + ownerName + ", Type: " + vehicleType;
    }
}