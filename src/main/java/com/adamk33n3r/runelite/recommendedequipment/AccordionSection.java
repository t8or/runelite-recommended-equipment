package com.adamk33n3r.runelite.recommendedequipment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class AccordionSection extends JPanel {
    private final JPanel contentPanel;
    private final JLabel titleLabel;
    private final JLabel arrowLabel;
    private boolean collapsed = true;

    public AccordionSection(String title, JPanel contentPanel, Consumer<AccordionSection> onToggle) {
        this.contentPanel = contentPanel;
        this.titleLabel = new JLabel(title);
        this.arrowLabel = new JLabel(Icons.CHEVRON_DOWN);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                toggle();
                onToggle.accept(AccordionSection.this);
            }
        };
        this.titleLabel.addMouseListener(mouseAdapter);
        this.arrowLabel.addMouseListener(mouseAdapter);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(this.arrowLabel, BorderLayout.EAST);
        top.add(this.titleLabel, BorderLayout.CENTER);
        top.setBorder(new HorizontalRuleBorder(5, HorizontalRuleBorder.BOTTOM));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(top);
        this.add(this.contentPanel);
        this.contentPanel.setVisible(false);
        this.contentPanel.setBorder(new HorizontalRuleBorder(5, HorizontalRuleBorder.BOTTOM));
    }

    public void toggle() {
        this.collapsed = !this.collapsed;
        this.contentPanel.setVisible(!this.collapsed);
        this.arrowLabel.setIcon(this.collapsed ? Icons.CHEVRON_DOWN : Icons.CHEVRON_UP);
    }
}
