package GUI;

import Algorithms.Treap;
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
                drawVisualization((Graphics2D) g);
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
        refreshInfoPanel();
        repaint();
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

    private void drawVisualization(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = canvasPanel.getWidth();
        int panelHeight = canvasPanel.getHeight();

        int gap = 20;
        int treapWidth = Math.max(430, panelWidth / 2 - 40);

        Rectangle parkingArea = new Rectangle(0, 0, panelWidth - treapWidth - gap, panelHeight);
        Rectangle treapArea = new Rectangle(panelWidth - treapWidth, 0, treapWidth, panelHeight);

        drawParkingArea(g2, parkingArea);
        drawTreapArea(g2, treapArea);
    }

    private void drawParkingArea(Graphics2D g2, Rectangle area) {
        int panelWidth = area.width;
        int panelHeight = area.height;

        int titleY = 35;
        int topMargin = 60;
        int sideMargin = 30;
        int bottomMargin = 30;

        int usableWidth = panelWidth - 2 * sideMargin;
        int usableHeight = panelHeight - topMargin - bottomMargin;

        int columns = 6;
        int rows = 2;

        int horizontalGap = Math.max(10, usableWidth / 50);
        int verticalGap = Math.max(28, usableHeight / 7);

        int spotWidth = (usableWidth - horizontalGap * (columns - 1)) / columns;
        int spotHeight = (usableHeight - verticalGap * (rows - 1)) / rows;

        spotWidth = Math.min(85, Math.max(58, spotWidth));
        spotHeight = Math.min(145, Math.max(82, spotHeight));

        int totalGridWidth = columns * spotWidth + (columns - 1) * horizontalGap;
        int totalGridHeight = rows * spotHeight + (rows - 1) * verticalGap;

        int startX = area.x + (panelWidth - totalGridWidth) / 2;
        int startY = area.y + topMargin + Math.max(0, (usableHeight - totalGridHeight) / 2);

        renderedSpots.clear();

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("Floor " + selectedFloor, area.x + 20, area.y + titleY);

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
            g2.setFont(new Font("Arial", Font.BOLD, Math.max(13, spotWidth / 5)));
            String text = String.valueOf(spotNumber);
            FontMetrics metrics = g2.getFontMetrics();
            int textX = rectangle.x + (rectangle.width - metrics.stringWidth(text)) / 2;
            int textY = rectangle.y + 24;
            g2.drawString(text, textX, textY);
        }
    }

    private void drawTreapArea(Graphics2D g2, Rectangle area) {
        g2.setColor(new Color(235, 235, 235));
        g2.fillRoundRect(area.x + 4, area.y + 8, area.width - 8, area.height - 16, 18, 18);

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Treap Structure", area.x + 18, area.y + 32);

        Treap.ViewNode<Integer, VehicleInfo> root = service.getTreapViewRoot(selectedFloor);

        if (root == null) {
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("Treap is empty.", area.x + 18, area.y + 60);
            return;
        }

        int centerX = area.x + area.width / 2;
        int startY = area.y + 80;
        int offset = Math.max(55, area.width / 4);

        drawTreapNode(g2, root, centerX, startY, offset);
    }

    private void drawTreapNode(
            Graphics2D g2,
            Treap.ViewNode<Integer, VehicleInfo> node,
            int x,
            int y,
            int offset
    ) {
        if (node == null) {
            return;
        }

        int nextY = y + 72;
        int nextOffset = Math.max(38, offset / 2);

        if (node.left != null) {
            int childX = x - offset;
            g2.setColor(Color.BLACK);
            g2.drawLine(x, y, childX, nextY);
            drawTreapNode(g2, node.left, childX, nextY, nextOffset);
        }

        if (node.right != null) {
            int childX = x + offset;
            g2.setColor(Color.BLACK);
            g2.drawLine(x, y, childX, nextY);
            drawTreapNode(g2, node.right, childX, nextY, nextOffset);
        }

        int radius = 22;

        g2.setColor(new Color(90, 170, 240));
        g2.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x - radius, y - radius, radius * 2, radius * 2);

        g2.setFont(new Font("Arial", Font.BOLD, 11));
        String keyText = String.valueOf(node.key);
        FontMetrics keyMetrics = g2.getFontMetrics();
        int keyX = x - keyMetrics.stringWidth(keyText) / 2;
        int keyY = y - 2;
        g2.drawString(keyText, keyX, keyY);

        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        String priorityText = "p=" + node.priority;
        FontMetrics priorityMetrics = g2.getFontMetrics();
        int priorityX = x - priorityMetrics.stringWidth(priorityText) / 2;
        int priorityY = y + 12;
        g2.drawString(priorityText, priorityX, priorityY);
    }

    @Override
    public void repaint() {
        super.repaint();
        if (canvasPanel != null) {
            canvasPanel.repaint();
        }
    }
}