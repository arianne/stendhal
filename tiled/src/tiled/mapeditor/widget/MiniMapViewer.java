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

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tiled.view.MapView;



public class MiniMapViewer extends JPanel
{
  private static final long serialVersionUID = -1243207988158851225L;

  public static final int MAX_HEIGHT = 150;

  private MapView mapView;
  private JScrollPane mapScrollPane;

  /** last viewpoint in the map editing panel */
  private Point lastViewPoint;
  
  public MiniMapViewer()
  {
    setSize(MAX_HEIGHT, MAX_HEIGHT);
  }

  public void setView(MapView view)
  {
    mapView = view;
    revalidate();
  }
  
  public Dimension getPreferredSize()
  {
    if (mapView == null)
    {
      return new Dimension(100, 100);
    }
    Image image = mapView.getMinimap();
    if (image == null)
    {
      return new Dimension(100, 100);
    }
    return new Dimension(image.getWidth(null), image.getHeight(null));
  }

  public void setMainPanel(JScrollPane main)
  {
    mapScrollPane = main;
  }
    
  /** clears the background */
  private void clearBackground(Graphics g)
  {
    Rectangle clip = g.getClipBounds();
    g.setColor(Color.BLACK);
    g.fillRect(clip.x,clip.y,clip.width, clip.height);
  }

  public void paintComponent(Graphics g)
  {
    clearBackground(g);
    if (mapView == null || mapView.getMinimap() == null)
    {
      return;
    }

    ((Graphics2D)g).drawImage(mapView.getMinimap(),0,0,null);

    if (mapScrollPane != null)
    {
      g.setColor(Color.yellow);
      Point viewPoint = mapScrollPane.getViewport().getViewPosition();
      Dimension viewSize = mapScrollPane.getViewport().getExtentSize();

      double scale = mapView.getMinimapScale() / mapView.getScale();

      if (viewPoint != null && viewSize != null)
      {
        Rectangle rect = new Rectangle((int)((viewPoint.x-1)    * scale),(int)((viewPoint.y-1)     * scale),
                                       (int)((viewSize.width-1) * scale),(int)((viewSize.height-1) * scale));

        // only update scrolling when the main viewport has changed
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
        if (!viewPoint.equals(lastViewPoint))
        {
          scrollRectToVisible(rect);
          repaint();
        }
        lastViewPoint = viewPoint;
      }
    }
  }
}
