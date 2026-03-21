package GUI;

import Model.VehicleInfo;
import Service.ParkingService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingFloorPanel extends JPanel {

    private final ParkingService service;
    private final ResultPanel resultPanel;

    private int selectedFloor = 1;
    private Integer selectedSpot = null;
    private Integer nearestFreeSpot = null;
    private Integer suggestedSpot = null;

    private final List<Rectangle> renderedSpots = new ArrayList<>();

    private final JLabel titleLabel = new JLabel("No spot selected");
    private final JTextArea infoArea = new JTextArea();

    private final JButton occupyButton = new JButton("Reserve Selected Spot");
    private final JButton releaseButton = new JButton("Release Selected Spot");
    private final JButton nearestButton = new JButton("Find Nearest Free Spot");
    private final JButton goToButton = new JButton("Go To Suggested Spot");

    private final JPanel canvasPanel;

    public ParkingFloorPanel(ParkingService service, ResultPanel resultPanel) {
        this.service = service;
        this.resultPanel = resultPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(new Color(245, 245, 245));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        infoArea.setEditable(false);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 13));
        infoArea.setRows(4);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JPanel topTextPanel = new JPanel(new BorderLayout());
        topTextPanel.setOpaque(false);
        topTextPanel.add(titleLabel, BorderLayout.NORTH);
        topTextPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        occupyButton.addActionListener(e -> occupySelectedSpot());
        releaseButton.addActionListener(e -> releaseSelectedSpot());
        nearestButton.addActionListener(e -> findNearestFreeSpot());
        goToButton.addActionListener(e -> goToSuggestedSpot());

        buttonPanel.add(occupyButton);
        buttonPanel.add(releaseButton);
        buttonPanel.add(nearestButton);
        buttonPanel.add(goToButton);

        infoPanel.add(topTextPanel, BorderLayout.CENTER);
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);

        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawParking((Graphics2D) g);
            }
        };
        canvasPanel.setBackground(new Color(245, 245, 245));

        canvasPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                handleClick(e.getPoint());
            }
        });

        add(infoPanel, BorderLayout.NORTH);
        add(canvasPanel, BorderLayout.CENTER);

        refreshInfoPanel();
    }

    public void setSelectedFloor(int floor) {
        this.selectedFloor = floor;
        this.selectedSpot = null;
        this.nearestFreeSpot = null;
        this.suggestedSpot = null;
        refreshInfoPanel();
        repaint();
    }

    public Integer getSelectedSpot() {
        return selectedSpot;
    }

    public void highlightNearestFreeSpot(int spot) {
        nearestFreeSpot = spot;
        suggestedSpot = spot;
        repaint();
        refreshInfoPanel();
    }

    public void selectSpot(int spot) {
        selectedSpot = spot;
        nearestFreeSpot = null;
        suggestedSpot = null;

        VehicleInfo info = service.getSpotInfo(selectedFloor, selectedSpot);
        if (info == null) {
            resultPanel.showText("Selected spot: " + selectedSpot + "\nStatus: Free");
        } else {
            resultPanel.showText("Selected spot: " + selectedSpot + "\nStatus: Occupied\n" + info);
        }

        refreshInfoPanel();
        repaint();
    }

    private void handleClick(Point point) {
        for (int i = 0; i < renderedSpots.size(); i++) {
            if (renderedSpots.get(i).contains(point)) {
                selectSpot(i + 1);
                return;
            }
        }
    }

    private void occupySelectedSpot() {
        if (selectedSpot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        String plate = askRequiredText("License plate:");
        if (plate == null) {
            return;
        }

        String owner = askRequiredText("Owner name:");
        if (owner == null) {
            return;
        }

        String type = askRequiredText("Vehicle type:");
        if (type == null) {
            return;
        }

        String result = service.occupySpot(
                selectedFloor,
                selectedSpot,
                new VehicleInfo(plate, owner, type)
        );

        resultPanel.showText(result);
        refreshInfoPanel();
        repaint();
    }

    private void releaseSelectedSpot() {
        if (selectedSpot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        String result = service.releaseSpot(selectedFloor, selectedSpot);
        resultPanel.showText(result);
        refreshInfoPanel();
        repaint();
    }

    private void findNearestFreeSpot() {
        if (selectedSpot == null) {
            resultPanel.showText("Please select a parking spot first.");
            return;
        }

        int nearest = service.findNearestFreeSpot(selectedFloor, selectedSpot);

        if (nearest == -1) {
            suggestedSpot = null;
            goToButton.setVisible(false);
            resultPanel.showText("No free spot found on this floor.");
        } else if (nearest == selectedSpot && !service.isOccupied(selectedFloor, selectedSpot)) {
            suggestedSpot = null;
            goToButton.setVisible(false);
            resultPanel.showText("Selected spot is already free.");
        } else {
            highlightNearestFreeSpot(nearest);
            resultPanel.showText("Nearest free spot to " + selectedSpot + " is " + nearest + ".");
        }
    }

    private void goToSuggestedSpot() {
        if (suggestedSpot != null) {
            selectSpot(suggestedSpot);
        }
    }

    private String askRequiredText(String message) {
        String value = JOptionPane.showInputDialog(this, message);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private void refreshInfoPanel() {
        suggestedSpot = nearestFreeSpot;

        if (selectedSpot == null) {
            titleLabel.setText("No spot selected");
            infoArea.setText("Click a parking spot in the visualization area.");
            occupyButton.setVisible(false);
            releaseButton.setVisible(false);
            nearestButton.setVisible(false);
            goToButton.setVisible(false);
            return;
        }

        titleLabel.setText("Parking Spot " + selectedSpot + " (Floor " + selectedFloor + ")");
        VehicleInfo info = service.getSpotInfo(selectedFloor, selectedSpot);

        if (info == null) {
            infoArea.setText("Status: FREE\nYou can reserve this spot.");
            occupyButton.setVisible(true);
            releaseButton.setVisible(false);
            nearestButton.setVisible(false);
        } else {
            infoArea.setText("Status: OCCUPIED\n" + info);
            occupyButton.setVisible(false);
            releaseButton.setVisible(true);
            nearestButton.setVisible(true);
        }

        goToButton.setVisible(suggestedSpot != null);
    }

    private void drawParking(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = canvasPanel.getWidth();
        int panelHeight = canvasPanel.getHeight();

        int titleY = 35;
        int topMargin = 60;
        int sideMargin = 40;
        int bottomMargin = 30;

        int usableWidth = panelWidth - 2 * sideMargin;
        int usableHeight = panelHeight - topMargin - bottomMargin;

        int columns = 6;
        int rows = 2;

        int horizontalGap = Math.max(16, usableWidth / 45);
        int verticalGap = Math.max(45, usableHeight / 5);

        int spotWidth = (usableWidth - horizontalGap * (columns - 1)) / columns;
        int spotHeight = (usableHeight - verticalGap * (rows - 1)) / rows;

        spotWidth = Math.min(110, Math.max(75, spotWidth));
        spotHeight = Math.min(170, Math.max(100, spotHeight));

        int totalGridWidth = columns * spotWidth + (columns - 1) * horizontalGap;
        int totalGridHeight = rows * spotHeight + (rows - 1) * verticalGap;

        int startX = (panelWidth - totalGridWidth) / 2;
        int startY = topMargin + Math.max(0, (usableHeight - totalGridHeight) / 2);

        renderedSpots.clear();

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("Floor " + selectedFloor, 20, titleY);

        for (int i = 0; i < 12; i++) {
            int row = i / columns;
            int column = i % columns;

            int x = startX + column * (spotWidth + horizontalGap);
            int y = startY + row * (spotHeight + verticalGap);

            Rectangle rectangle = new Rectangle(x, y, spotWidth, spotHeight);
            renderedSpots.add(rectangle);

            int spotNumber = i + 1;
            boolean occupied = service.isOccupied(selectedFloor, spotNumber);
            boolean selected = selectedSpot != null && selectedSpot == spotNumber;
            boolean nearest = nearestFreeSpot != null && nearestFreeSpot == spotNumber;

            if (nearest) {
                g2.setColor(new Color(46, 204, 113));
            } else if (occupied) {
                g2.setColor(new Color(220, 70, 70));
            } else {
                g2.setColor(new Color(220, 220, 220));
            }

            g2.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

            if (selected) {
                g2.setColor(new Color(52, 152, 219));
                g2.setStroke(new BasicStroke(4f));
            } else {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
            }

            g2.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, Math.max(14, spotWidth / 5)));
            String text = String.valueOf(spotNumber);
            FontMetrics fontMetrics = g2.getFontMetrics();
            int textX = rectangle.x + (rectangle.width - fontMetrics.stringWidth(text)) / 2;
            int textY = rectangle.y + 28;
            g2.drawString(text, textX, textY);
        }
    }

    @Override
    public void repaint() {
        super.repaint();
        if (canvasPanel != null) {
            canvasPanel.repaint();
        }
    }
}