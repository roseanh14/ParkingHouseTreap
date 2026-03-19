package GUI;

import Model.ParkingSpot;
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

    private final List<Rectangle> renderedSpots = new ArrayList<>();

    public ParkingFloorPanel(ParkingService service, ResultPanel resultPanel) {
        this.service = service;
        this.resultPanel = resultPanel;

        setBackground(new Color(245, 245, 245));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                handleClick(e.getPoint());
            }
        });
    }

    public void setSelectedFloor(int floor) {
        this.selectedFloor = floor;
        this.selectedSpot = null;
        this.nearestFreeSpot = null;
        repaint();
    }

    public Integer getSelectedSpot() {
        return selectedSpot;
    }

    public int getSelectedFloor() {
        return selectedFloor;
    }

    public void highlightNearestFreeSpot(int spot) {
        nearestFreeSpot = spot;
        repaint();
    }

    private void handleClick(Point point) {
        for (int i = 0; i < renderedSpots.size(); i++) {
            if (renderedSpots.get(i).contains(point)) {
                selectedSpot = i + 1;
                nearestFreeSpot = null;

                VehicleInfo info = service.getSpotInfo(selectedFloor, selectedSpot);

                if (info == null) {
                    resultPanel.showText("Selected spot: " + selectedSpot + "\nStatus: Free");
                } else {
                    resultPanel.showText("Selected spot: " + selectedSpot + "\nStatus: Occupied\n" + info);
                }

                repaint();
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelW = getWidth();
            int panelH = getHeight();

            int titleY = 40;
            int topMargin = 70;
            int sideMargin = 40;
            int bottomMargin = 40;

            int usableW = panelW - 2 * sideMargin;
            int usableH = panelH - topMargin - bottomMargin;

            int columns = 6;
            int rows = 2;

            int horizontalGap = Math.max(16, usableW / 40);
            int verticalGap = Math.max(40, usableH / 5);

            int spotW = (usableW - horizontalGap * (columns - 1)) / columns;
            int spotH = (usableH - verticalGap * (rows - 1)) / rows;

            spotW = Math.min(110, Math.max(70, spotW));
            spotH = Math.min(160, Math.max(90, spotH));

            int totalGridW = columns * spotW + (columns - 1) * horizontalGap;
            int totalGridH = rows * spotH + (rows - 1) * verticalGap;

            int startX = (panelW - totalGridW) / 2;
            int startY = topMargin + Math.max(0, (usableH - totalGridH) / 2);

            renderedSpots.clear();

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.drawString("Floor " + selectedFloor, 20, titleY);

            for (int i = 0; i < 12; i++) {
                int row = i / columns;
                int col = i % columns;

                int x = startX + col * (spotW + horizontalGap);
                int y = startY + row * (spotH + verticalGap);

                Rectangle r = new Rectangle(x, y, spotW, spotH);
                renderedSpots.add(r);

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

                g2.fillRect(r.x, r.y, r.width, r.height);

                if (selected) {
                    g2.setColor(new Color(52, 152, 219));
                    g2.setStroke(new BasicStroke(4f));
                } else {
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2f));
                }

                g2.drawRect(r.x, r.y, r.width, r.height);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, Math.max(14, spotW / 5)));
                String text = String.valueOf(spotNumber);
                FontMetrics fm = g2.getFontMetrics();
                int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
                int ty = r.y + 25;
                g2.drawString(text, tx, ty);
            }

        } finally {
            g2.dispose();
        }
    }
}