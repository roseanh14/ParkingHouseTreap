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

    private final ParkingService service;
    private final ParkingFloorPanel floorPanel;
    private final ResultPanel resultPanel;

    private final JComboBox<Integer> floorCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});

    public ControlPanel(ParkingService service, ParkingFloorPanel floorPanel, ResultPanel resultPanel) {
        this.service = service;
        this.floorPanel = floorPanel;
        this.resultPanel = resultPanel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(DARK);
        setPreferredSize(new Dimension(260, 900));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        floorCombo.setMaximumSize(new Dimension(220, 34));
        floorCombo.setPreferredSize(new Dimension(220, 34));
        floorCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        floorCombo.addActionListener(e -> floorPanel.setSelectedFloor((Integer) floorCombo.getSelectedItem()));

        add(section("1. Floor Selection",
                label("Choose floor:"),
                floorCombo
        ));

        add(section("2. File",
                button("Load Initial Data", this::loadInitialData),
                button("Save Action Log", this::saveActionLog)
        ));

        add(section("3. Spot Actions",
                button("Occupy Selected Spot", this::occupySelectedSpot),
                button("Release Selected Spot", this::releaseSelectedSpot),
                button("Find Nearest Free Spot", this::findNearestFreeSpot)
        ));

        add(section("4. Treap Operations",
                button("List Occupied Spots", this::listOccupiedSpots),
                button("Validate Treap", this::validateTreap)
        ));

        add(Box.createVerticalGlue());
    }

    private JPanel section(String title, JComponent... items) {
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
        panel.setMaximumSize(new Dimension(240, 300));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < items.length; i++) {
            panel.add(items[i]);
            if (i < items.length - 1) {
                panel.add(Box.createVerticalStrut(8));
            }
        }
        panel.add(Box.createVerticalStrut(4));
        return panel;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(220, 36));
        button.setPreferredSize(new Dimension(220, 36));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(60, 78, 108));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
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
                ActionLogWriter.saveToFile(chooser.getSelectedFile().getAbsolutePath(), service.getActionLog());
                resultPanel.showText("Action log saved successfully.");
            } catch (Exception e) {
                resultPanel.showText("Error while saving action log: " + e.getMessage());
            }
        }
    }

    private void occupySelectedSpot() {
        Integer floor = floorPanel.getSelectedFloor();
        Integer spot = floorPanel.getSelectedSpot();

        if (spot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        String plate = JOptionPane.showInputDialog(this, "License plate:");
        if (plate == null || plate.isBlank()) return;

        String owner = JOptionPane.showInputDialog(this, "Owner name:");
        if (owner == null || owner.isBlank()) return;

        String type = JOptionPane.showInputDialog(this, "Vehicle type:");
        if (type == null || type.isBlank()) return;

        String result = service.occupySpot(floor, spot, new VehicleInfo(plate, owner, type));
        resultPanel.showText(result);
        floorPanel.repaint();
    }

    private void releaseSelectedSpot() {
        Integer floor = floorPanel.getSelectedFloor();
        Integer spot = floorPanel.getSelectedSpot();

        if (spot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        String result = service.releaseSpot(floor, spot);
        resultPanel.showText(result);
        floorPanel.repaint();
    }

    private void listOccupiedSpots() {
        resultPanel.showText(service.listOccupiedSpots(floorPanel.getSelectedFloor()));
    }

    private void findNearestFreeSpot() {
        Integer floor = floorPanel.getSelectedFloor();
        Integer spot = floorPanel.getSelectedSpot();

        if (spot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        int nearest = service.findNearestFreeSpot(floor, spot);
        if (nearest == -1) {
            resultPanel.showText("No free spot found on this floor.");
        } else {
            resultPanel.showText("Nearest free spot to " + spot + " is " + nearest + ".");
            floorPanel.highlightNearestFreeSpot(nearest);
        }
    }

    private void validateTreap() {
        resultPanel.showText(service.validateTreap(floorPanel.getSelectedFloor()));
    }
}