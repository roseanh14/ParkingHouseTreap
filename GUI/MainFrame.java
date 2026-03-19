package GUI;

import Model.ParkingHouse;
import Service.ParkingService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Parking House - Treap");

        ParkingHouse parkingHouse = new ParkingHouse();
        ParkingService service = new ParkingService(parkingHouse);

        ResultPanel resultPanel = new ResultPanel();
        ParkingFloorPanel floorPanel = new ParkingFloorPanel(service, resultPanel);
        ControlPanel controlPanel = new ControlPanel(service, floorPanel, resultPanel);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Parking Visualization", floorPanel);
        tabs.addTab("Results and Action Log", resultPanel);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.WEST);
        add(tabs, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}