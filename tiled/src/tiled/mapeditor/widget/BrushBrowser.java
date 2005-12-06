/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.widget;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.brush.ShapeBrush;

/**
 * A panel that allows selecting a brush from a set of presets.
 */
public class BrushBrowser extends JPanel implements MouseInputListener
{
  private static final long serialVersionUID = -8809620337284162963L;

    private int maxWidth = 25;
    private Brush selectedBrush;
    private List<Brush> brushes;

    public BrushBrowser() {
        super();
        brushes = new ArrayList<Brush>();
        initPresets();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public Dimension getPreferredSize() {
        int perLine = getWidth() / maxWidth;
        if (perLine > 0) {
            int lines = (brushes.size() + (perLine - 1)) / perLine;
            return new Dimension(maxWidth, maxWidth * lines);
        } else {
            return new Dimension(maxWidth, 150);
        }
    }

    private void initPresets() {
        int[] dimensions = { 1, 2, 4, 8, 12, 20 };

        for (int n = 1; n < dimensions.length; n++) {
            ShapeBrush b = new ShapeBrush();
            b.makeCircleBrush(dimensions[n] / 2);
            brushes.add(b);
        }

        for (int n = 0; n < dimensions.length; n++) {
            ShapeBrush b = new ShapeBrush();
            b.makeQuadBrush(new Rectangle(0, 0, dimensions[n], dimensions[n]));
            brushes.add(b);
        }
    }

    public void paint(Graphics g) {
        Rectangle clipRect = g.getClipBounds();
        g.setColor(Color.white);
        g.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.black);

        // Draw the brushes
        Iterator<Brush> itr = brushes.iterator();
        int x = 0, y = 0;
        while (itr.hasNext()) {
            Brush b = itr.next();
            Rectangle bb = b.getBounds();
            b.paint(g,
                    x + ((maxWidth / 2) - bb.width / 2),
                    y + ((maxWidth / 2) - bb.width / 2));

            if (b == selectedBrush) {
                g.drawRect(x, y, maxWidth, maxWidth);
            }

            x += maxWidth;
            if (x + maxWidth > getWidth()) {
                x = 0;
                y += maxWidth;
            }
        }
    }

    public void setSelectedBrush(Brush b) {
        Iterator<Brush> itr = brushes.iterator();
        while (itr.hasNext()) {
            Brush br = itr.next();
            if (br.equals(b)) {
                selectedBrush = br;
                break;
            }
        }
    }

    public Brush getSelectedBrush() {
        return selectedBrush;
    }

    public void mouseClicked(MouseEvent e) {
        int perLine = getWidth() / maxWidth;
        int x = e.getX() / maxWidth;
        int y = e.getY() / maxWidth;
        int selectedIndex =
            y * perLine + ((x > (perLine - 1)) ? (perLine - 1) : x);

        if (selectedIndex >= 0 && selectedIndex < brushes.size()) {
            Brush previousBrush = selectedBrush;
            selectedBrush = brushes.get(selectedIndex);
            firePropertyChange("selectedbrush", previousBrush, selectedBrush);
            repaint();
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        mouseClicked(e);
    }

    public void mouseMoved(MouseEvent e) {
    }
}
