package Data;

import Model.VehicleInfo;
import Service.ParkingService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {
    public static String loadFromFile(String path, ParkingService service) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");

                if (parts.length != 5) {
                    sb.append("Skipped invalid line: ").append(line).append("\n");
                    continue;
                }

                int floor = Integer.parseInt(parts[0]);
                int spot = Integer.parseInt(parts[1]);
                String plate = parts[2];
                String owner = parts[3];
                String type = parts[4];

                sb.append(service.occupySpot(floor, spot, new VehicleInfo(plate, owner, type))).append("\n");
            }
        } catch (IOException e) {
            return "Error while loading file: " + e.getMessage();
        }

        return sb.toString();
    }
}