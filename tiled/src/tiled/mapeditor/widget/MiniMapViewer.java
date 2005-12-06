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
 *  Rainer Deyke <rainerd@eldwood.com>
 *  
 *  modified for stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.widget;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tiled.view.MapView;


public class MiniMapViewer extends JPanel
{
  private static final long serialVersionUID = -1243207988158851225L;

    public static final int MAX_HEIGHT = 150;

    private MapView myView;
    private JScrollPane mainPanel;
    private double scale = 0.0625;
    private BufferedImage renderedMap;
    
    public MiniMapViewer() {
        setSize(MAX_HEIGHT, MAX_HEIGHT);
    }

    public MiniMapViewer(MapView view) {
        this();
        setView(view);
    }

    public void setView(MapView view) {
//        myView = view;
//        myView.setZoom(scale);
//        Dimension d = myView.getPreferredSize();
//        renderedMap = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
//        //scale = MAX_HEIGHT / (double)d.height;
//        Graphics2D g = renderedMap.createGraphics();
//        g.setClip(0, 0, d.width, d.height);
//        myView.paint(g);
    }
    
    public Dimension getPreferredSize() {
        if(myView != null) {
            return myView.getPreferredSize();
        }
        return new Dimension(0, 0);
    }
    
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    
    public void setMainPanel(JScrollPane main) {
        mainPanel = main;
    }

    public void refresh() {
        if(renderedMap != null && myView != null) {
            Dimension d = myView.getPreferredSize();
	        Graphics2D g = renderedMap.createGraphics();
	        g.setClip(0, 0, d.width, d.height);
	        myView.paint(g);
        }
    }
    
    public void paint(Graphics g) {
        /*if (myView != null) {
            myView.paint(g);
        }*/
        if(renderedMap != null) {
            g.drawImage(renderedMap, 0, 0, null);
        }
        
        if (mainPanel != null) {
            g.setColor(Color.yellow);
            Rectangle viewArea = mainPanel.getViewport().getBounds();
            if (viewArea != null) {
                g.drawRect(
                        (int)((viewArea.x-1) * scale),
                        (int)((viewArea.y-1) * scale),
                        (int)((viewArea.width-1) * scale),
                        (int)((viewArea.height-1) * scale));
            }
        }
    }
}
