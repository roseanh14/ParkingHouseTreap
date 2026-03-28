package GUI;

import Data.ActionLogWriter;
import Data.DataLoader;
import Model.VehicleInfo;
import Service.ParkingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlPanel extends JPanel {

    private static final Color DARK = new Color(30, 40, 60);
    private static final Color DARK2 = new Color(40, 52, 75);
    private static final Color BUTTON_COLOR = new Color(60, 78, 108);

    private final ParkingService service;
    private final ParkingFloorPanel floorPanel;
    private final ResultPanel resultPanel;

    private final JComboBox<Integer> floorCombo;

    public ControlPanel(ParkingService service, ParkingFloorPanel floorPanel, ResultPanel resultPanel) {
        this.service = service;
        this.floorPanel = floorPanel;
        this.resultPanel = resultPanel;
        this.floorCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(DARK);
        setPreferredSize(new Dimension(260, 900));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        floorCombo.setMaximumSize(new Dimension(220, 34));
        floorCombo.setPreferredSize(new Dimension(220, 34));
        floorCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        floorCombo.setSelectedIndex(0);
        floorCombo.addActionListener(e -> {
            Integer selectedFloor = (Integer) floorCombo.getSelectedItem();
            if (selectedFloor != null) {
                floorPanel.setSelectedFloor(selectedFloor);
            }
        });

        JLabel floorLabel = new JLabel("Choose floor:");
        floorLabel.setForeground(Color.LIGHT_GRAY);
        floorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        floorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(section(
                "1. Floor Selection",
                floorLabel,
                floorCombo
        ));

        add(section(
                "2. File",
                createButton("Load Initial Data", this::loadInitialData),
                createButton("Save Action Log", this::saveActionLog)
        ));

        add(section(
                "3. Spot Actions",
                createButton("Occupy Selected Spot", this::occupySelectedSpot),
                createButton("Release Selected Spot", this::releaseSelectedSpot),
                createButton("Find Nearest Free Spot", this::findNearestFreeSpot)
        ));

        add(section(
                "4. Treap Operations",
                createButton("List Occupied Spots", this::listOccupiedSpots),
                createButton("Validate Treap", this::validateTreap),
                createButton("Show Treap", this::showTreap)
        ));

        add(Box.createVerticalGlue());
    }

    private JPanel section(String title, JComponent... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARK2);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(90, 110, 140)),
                        title,
                        0,
                        0,
                        new Font("Arial", Font.BOLD, 12),
                        Color.LIGHT_GRAY
                ),
                new EmptyBorder(8, 8, 8, 8)
        ));
        panel.setMaximumSize(new Dimension(240, 320));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < components.length; i++) {
            panel.add(components[i]);
            if (i < components.length - 1) {
                panel.add(Box.createVerticalStrut(8));
            }
        }

        panel.add(Box.createVerticalStrut(4));
        return panel;
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(220, 36));
        button.setPreferredSize(new Dimension(220, 36));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }

    private int getSelectedFloorOrShowMessage() {
        Integer selectedFloor = (Integer) floorCombo.getSelectedItem();
        if (selectedFloor == null) {
            resultPanel.showText("Please select a floor.");
            return -1;
        }
        return selectedFloor;
    }

    private void loadInitialData() {
        resultPanel.showText(DataLoader.loadFromFile("test-data.txt", service));
        floorPanel.repaint();
    }

    private void saveActionLog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("action-log.txt"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = chooser.getSelectedFile().getAbsolutePath();
                ActionLogWriter.saveToFile(path, service.getActionLog());
                resultPanel.showText("Action log saved successfully.");
            } catch (Exception exception) {
                resultPanel.showText("Error while saving action log: " + exception.getMessage());
            }
        }
    }

    private void occupySelectedSpot() {
        int floor = getSelectedFloorOrShowMessage();
        if (floor == -1) {
            return;
        }

        Integer selectedSpot = floorPanel.getSelectedSpot();
        if (selectedSpot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        String plate = JOptionPane.showInputDialog(this, "License plate:");
        if (plate == null || plate.isBlank()) {
            return;
        }

        String owner = JOptionPane.showInputDialog(this, "Owner name:");
        if (owner == null || owner.isBlank()) {
            return;
        }

        String type = JOptionPane.showInputDialog(this, "Vehicle type:");
        if (type == null || type.isBlank()) {
            return;
        }

        VehicleInfo info = new VehicleInfo(plate, owner, type);
        String result = service.occupySpot(floor, selectedSpot, info);
        resultPanel.showText(result);
        floorPanel.repaint();
    }

    private void releaseSelectedSpot() {
        int floor = getSelectedFloorOrShowMessage();
        if (floor == -1) {
            return;
        }

        Integer selectedSpot = floorPanel.getSelectedSpot();
        if (selectedSpot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        String result = service.releaseSpot(floor, selectedSpot);
        resultPanel.showText(result);
        floorPanel.repaint();
    }

    private void findNearestFreeSpot() {
        int floor = getSelectedFloorOrShowMessage();
        if (floor == -1) {
            return;
        }

        Integer selectedSpot = floorPanel.getSelectedSpot();
        if (selectedSpot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        int nearestSpot = service.findNearestFreeSpot(floor, selectedSpot);

        if (nearestSpot == -1) {
            resultPanel.showText("No free spot found on this floor.");
            return;
        }

        resultPanel.showText("Nearest free spot to " + selectedSpot + " is " + nearestSpot + ".");
        floorPanel.highlightNearestFreeSpot(nearestSpot);
    }


    //u nasledujicich tri metod udelat aby v GUI automaticky preklidlo na zalozku s result logami. Jinak by to vypadalo
    //ze tlacitka List Occupied Spots, Validate Treap a Show Treap nic nedelaji
    private void listOccupiedSpots() {
        int floor = getSelectedFloorOrShowMessage();
        if (floor == -1) {
            return;
        }

        resultPanel.showText(service.listOccupiedSpots(floor));
    }

    private void validateTreap() {
        int floor = getSelectedFloorOrShowMessage();
        if (floor == -1) {
            return;
        }

        resultPanel.showText(service.validateTreap(floor));
    }

    private void showTreap() {
        int floor = getSelectedFloorOrShowMessage();
        if (floor == -1) {
            return;
        }

        resultPanel.showText("Treap structure for floor " + floor + ":\n\n" + service.printTreap(floor));
    }
}