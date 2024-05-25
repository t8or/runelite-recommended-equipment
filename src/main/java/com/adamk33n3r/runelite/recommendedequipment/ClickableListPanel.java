package com.adamk33n3r.runelite.recommendedequipment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class ClickableListPanel extends JPanel {
    public ClickableListPanel(String title, JScrollPane scrollPane) {
        super();
        this.setLayout(new BorderLayout(5, 5));
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 12, 0, 5));
        header.add(new JLabel(title), BorderLayout.WEST);
//        JLabel sortLabel = new JLabel("Sort A-Z", Icons.CHEVRON_DOWN, SwingConstants.RIGHT);
//        sortLabel.setHorizontalTextPosition(SwingConstants.LEFT);
//        header.add(sortLabel, BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void setClickableList(JScrollPane scrollPane) {
        this.add(scrollPane, BorderLayout.CENTER);
    }
}
