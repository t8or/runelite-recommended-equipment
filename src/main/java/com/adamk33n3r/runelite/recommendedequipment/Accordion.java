package com.adamk33n3r.runelite.recommendedequipment;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class Accordion extends ScrollablePanel {
    private static final Border VIEWPORT_BORDER = new EmptyBorder(0, 0, 0, 5);
    public Accordion() {
        this.setLayout(new StretchedStackedLayout(5));
    }

    public void addSection(String title, JPanel content) {
        AccordionSection section = new AccordionSection(title, content, this::onSectionToggle);
        this.add(section);
    }

    public void onSectionToggle(AccordionSection section) {
        // A little glitchy, but it works
        SwingUtilities.invokeLater(() -> {
            Container scrollPaneContainer = SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
            if (scrollPaneContainer == null) {
                return;
            }

            JScrollPane scrollPane = (JScrollPane) scrollPaneContainer;
            if (scrollPane.getVerticalScrollBar().isVisible()) {
                scrollPane.setViewportBorder(VIEWPORT_BORDER);
            } else {
                scrollPane.setViewportBorder(null);
            }
            this.revalidate();

            // Revalidate all sections since scrollbar changed
            SwingUtilities.invokeLater(() -> {
                Arrays.stream(this.getComponents())
                    .forEach(Component::revalidate);
            });
        });
    }
}
