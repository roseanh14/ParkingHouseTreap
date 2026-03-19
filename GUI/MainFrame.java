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

        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, floorPanel, resultPanel);
        centerSplit.setResizeWeight(0.72);
        centerSplit.setDividerSize(6);
        centerSplit.setBorder(null);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.WEST);
        add(centerSplit, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}