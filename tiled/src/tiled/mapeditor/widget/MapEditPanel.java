/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package tiled.mapeditor.widget;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tiled.core.MapLayer;
import tiled.core.TileLayer;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.util.MapModifyListener;
import tiled.view.test.MapView;

/**
 * This Panel contains the map editor itself.
 * The map is an abstract (drawable) container. This panel just manages the 
 * communication with the window toolkit.
 * 
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public class MapEditPanel extends JPanel implements MouseListener, MouseMotionListener
{
  private static final long serialVersionUID = -7165936206773083620L;

  /** the map view */
  private MapView mapView;
  /** the minimap panel */
  private MiniMapViewer miniMapViewer;
  
  /** list of modify listeners */
  private List<MapModifyListener> listeners;
  /** base map editor */
  private MapEditor mapEditor;
  /** last drawn cursor rectangle */
  private Rectangle lastDrawnCursor;
  /** should the cursor be shown? */
  private boolean showCursor;

  /** constuctor */
  public MapEditPanel(MapEditor mapEditor)
  {
    this.mapEditor = mapEditor;
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  /** sets the map view. Note that this clears all selected tiles */
  public void setMapView(MapView mapView)
  {
    this.mapView = mapView;
    miniMapViewer.setView(mapView);
    revalidate();
  }
  
  /** sets the minimap panel. */
  public void setMinimapPanel(MiniMapViewer miniMapViewer)
  {
    this.miniMapViewer = miniMapViewer;
  }

  /** registers a listener to be notified when the map content changes */
  public void registerMapModifyListener(MapModifyListener mapModifyListener)
  {
    if (!listeners.contains(mapModifyListener))
    {
      listeners.add(mapModifyListener);
    }
  }

  /** removed a registered listener */
  public void removeMapModifyListener(MapModifyListener mapModifyListener)
  {
    listeners.remove(mapModifyListener);
  }
  
  /** Draws the map */
  public void paintComponent(Graphics g)
  {
    if (mapView != null)
    {
      mapView.draw(g);
      if (miniMapViewer != null)
      {
        miniMapViewer.repaint();
      }
      
      if (showCursor)
      {
        Point p = getMousePosition();
        Brush brush = mapEditor.currentBrush;
        if (p != null && brush != null)
        {
          Rectangle cursor = mapView.tileToScreenRect(brush.getBounds());
          Point tilePoint = mapView.tileToScreenCoords(mapView.screenToTileCoords(p));
          cursor.translate(tilePoint.x, tilePoint.y);
          g.setColor(Color.WHITE);
          g.drawRect(cursor.x, cursor.y, cursor.width-1, cursor.height-1);
          lastDrawnCursor = cursor;
        }
      }
    }
  }

  /** repaints a portion of the map */
  public void repaintRegion(Rectangle affectedRegion)
  {
    if (mapView != null)
    {
      repaint(mapView.tileToScreenRect(affectedRegion));
      if (miniMapViewer != null)
      {
        miniMapViewer.repaint();
      }
    }
  }
  
  
  /** returns the prefered size of the panel */
  public Dimension getPreferredSize()
  {
    if (mapView != null)
    {
      return mapView.getSize();
    }
    return new Dimension(100,100);
  }

  /** repaints the last cursor position */
  private void repaintLastCursorPosition()
  {
    if (lastDrawnCursor != null)
    {
      repaint(lastDrawnCursor);
    }
  }
  
  /** draws at the point p (in screen coords) the current brush */
  private void drawTo(Point p)
  {
    if (p == null || mapView == null)
      return;
    
    Container parent = getParent();
    if (parent != null && parent instanceof JScrollPane)
    {
      JScrollPane pane = (JScrollPane) parent;
      Point other = pane.getViewport().getViewPosition();
      p.translate(other.x, other.y);
    }
    
    Point tile = mapView.screenToTileCoords(p);
    
    Brush brush = mapEditor.currentBrush;
    MapLayer layer = mapEditor.getCurrentLayer();
    if (layer instanceof TileLayer)
    {
      Rectangle affectedRegion = brush.commitPaint(mapEditor.currentMap, tile.x, tile.y, mapEditor.currentLayer);
      repaintRegion(affectedRegion);
    }
  }
  

  public void mouseClicked(MouseEvent e)
  {
    drawTo(e.getPoint());
  }

  public void mousePressed(MouseEvent e)
  {
  }

  public void mouseReleased(MouseEvent e)
  {
  }

  public void mouseEntered(MouseEvent e)
  {
    showCursor = true;
    repaintLastCursorPosition();
  }


  public void mouseExited(MouseEvent e)
  {
    showCursor = false;
    repaintLastCursorPosition();
  }

  public void mouseDragged(MouseEvent e) 
  {
    if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
    {
      drawTo(e.getPoint());
      repaintLastCursorPosition();
    }
  }

  public void mouseMoved(MouseEvent e)
  {
    if (mapView == null)
      return;
    
    Point p = getMousePosition();
    if (p != null)
    {
      Brush brush = mapEditor.currentBrush;
      if (brush != null)
      {
        Rectangle cursor = mapView.tileToScreenRect(brush.getBounds());
        Point tilePoint = mapView.tileToScreenCoords(mapView.screenToTileCoords(p));
        cursor.translate(tilePoint.x, tilePoint.y);
        
        repaintLastCursorPosition();
        repaint(cursor);
      }
    }
  }
  
}
