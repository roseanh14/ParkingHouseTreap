package GUI;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {

    private final JTextArea area = new JTextArea();

    public ResultPanel() {
        setLayout(new BorderLayout());

        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(10, 10, 10, 10));

        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    public void showText(String text) {
        area.setText(text == null ? "" : text);
        area.setCaretPosition(0);
    }

    public void appendText(String text) {
        if (area.getText().isEmpty()) {
            area.setText(text);
        } else {
            area.append("\n\n" + text);
        }
        area.setCaretPosition(area.getDocument().getLength());
    }
}