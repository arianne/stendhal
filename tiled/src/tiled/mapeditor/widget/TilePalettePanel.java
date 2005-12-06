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
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputListener;

import tiled.core.*;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.brush.MultiTileBrush;
import tiled.mapeditor.util.*;

public class TilePalettePanel extends JPanel implements MouseInputListener
{
  private static final long serialVersionUID = 364058467891505985L;

  private static final int MIN_TILES_PER_ROW = 10;

  /** the tileset for this panel */
  private TileSet           tileset;
  /** a list of listeners to be notified when the selected tile changes */
  private EventListenerList tileSelectionListeners;
  /** the currently selected tiles */
  private List<Tile> selectedTiles;
  /** brush to paint the selected tiles */
  private MultiTileBrush selectedBrush;
  /** indicator that the user is drawing a frame to select multiple tiles */
  private boolean dragInProgress;
  /** the point where the drag started */
  private Point dragStartPoint;
  /** the current endpoint of the drag operation */
  private Point currentDragPoint;
  
  /** */
  private int twidth;
  private int theight;

  private int tilesPerRow;




  public TilePalettePanel()
  {
    tileSelectionListeners = new EventListenerList();
    selectedTiles = new ArrayList<Tile>();
    selectedBrush = new MultiTileBrush();

    addMouseListener(this);
    addMouseMotionListener(this);
  }

  /**
   * Adds tile selection listener. The listener will be notified when the user
   * selects a tile.
   */
  public void addTileSelectionListener(TileSelectionListener l)
  {
    tileSelectionListeners.add(TileSelectionListener.class, l);
  }

  /**
   * Removes tile selection listener.
   */
  public void removeTileSelectionListener(TileSelectionListener l)
  {
    tileSelectionListeners.remove(TileSelectionListener.class, l);
  }

  protected void fireTileSelectionEvent(List<Tile> selectedTiles, Brush brush)
  {
    TileSelectionListener[] listeners = tileSelectionListeners.getListeners(TileSelectionListener.class);
    TileSelectionEvent event = null;

    for (TileSelectionListener listener : listeners)
    {
      if (event == null)
      {
        event = new TileSelectionEvent(this, new ArrayList<Tile>(selectedTiles), brush);
      }
      listener.tileSelected(event);
    }
  }

  /**
   * Change the tilesets displayed by this palette panel.
   */
  public void setTileset(TileSet set)
  {
    tileset = set;
    selectedTiles.clear();
    refreshTileProperties();
    setSize(getPreferredSize());
    repaint();
  }
  
  /**
   * 
   */
  private void refreshTileProperties()
  {
    twidth = tileset.getStandardWidth() + 1;
    theight = tileset.getTileHeightMax() + 1;

    tilesPerRow = tileset.getPreferredTilesPerRow();
    if (tilesPerRow == 0)
    {
      tilesPerRow = (getWidth() / twidth) - 1;
      if (tilesPerRow <= MIN_TILES_PER_ROW)
      {
        tilesPerRow = MIN_TILES_PER_ROW;
      }
    }
  }
  
  /** returns a rectangle for these points */
  private Rectangle getRectangle(Point p1, Point p2)
  {
    int x = p1.x;
    int y = p1.y;
    int w = p2.x - x;
    int h = p2.y - y;
    
    if (w < 0)
    {
      w = -w;
      x -= w;
    }
    if (h < 0)
    {
      h = -h;
      y -= h;
    }
    
    return new Rectangle(x,y,w,h);
  }

  /** returns the tile at the given position (or null if there is no tile) */
  public Tile getTileAtPoint(int x, int y)
  {
    Tile ret = null;

    int size = tileset.size();
    
    int tilex = x / twidth;
    int tiley = y / theight;
    
    if (tilex < tilesPerRow)
    {
      int tileNum = tiley * tilesPerRow + tilex;
      if (tileNum < size)
      {
        return tileset.getTile(tileNum);
      }
    }

    return ret;
  }
  

  /** paints the component */
  public void paint(Graphics g)
  {
    paintBackground(g);

    if (tileset == null)
    {
      return;
    }

    int gx = 0;
    int gy = 0;

    if (tileset != null)
    {
      // Draw the tiles
//      twidth = tileset.getStandardWidth() + 1;
//      maxHeight = tileset.getTileHeightMax() + 1;
      int width = getWidth() - twidth;

//      int tilesPerRow = tileset.getPreferredTilesPerRow();
      if (tilesPerRow > 0)
      {
        width = tilesPerRow * twidth - 1;
      }

      int tileAt = 0;

      for (Tile tile : tileset)
      {
        if (tile != null)
        {
          int x = gx;
          int y = gy + (theight - tile.getHeight());
          tile.drawRaw(g, x,y , 1.0);
          
        }
        gx += twidth;
        if (gx > width)
        {
          gy += theight;
          gx = 0;
        }
        tileAt++;
      }
      // draw selected tiles
      g.setColor(Color.YELLOW);
      
      for (Tile tile : selectedTiles)
      {
        int id = tile.getId();
        int x = id % tilesPerRow;
        int y = id / tilesPerRow;
        g.drawRect(x * twidth - 1, y * theight    , tile.getWidth() + 1,tile.getHeight() + 1);
        g.drawRect(x * twidth    , y * theight + 1, tile.getWidth() - 1,tile.getHeight() - 1);
      }
      
      // drag selection rectangle
      if (dragInProgress)
      {
        Rectangle rect = getRectangle(dragStartPoint, currentDragPoint);
        
        g.setColor(Color.WHITE);
        g.drawRect(rect.x  ,rect.y  ,rect.width  , rect.height);
        g.drawRect(rect.x+1,rect.y+1,rect.width-2, rect.height-2);
      }
    }
  }

  /**
   * Draws checkerboard background.
   */
  private void paintBackground(Graphics g)
  {
    Rectangle clip = g.getClipBounds();
    int side = 10;

    int startX = clip.x / side;
    int startY = clip.y / side;
    int endX = (clip.x + clip.width) / side + 1;
    int endY = (clip.y + clip.height) / side + 1;

    // Fill with white background
    g.setColor(Color.WHITE);
    g.fillRect(clip.x, clip.y, clip.width, clip.height);

    // Draw darker squares
    g.setColor(Color.LIGHT_GRAY);
    for (int y = startY; y < endY; y++)
    {
      for (int x = startX; x < endX; x++)
      {
        if ((y + x) % 2 == 1)
        {
          g.fillRect(x * side, y * side, side, side);
        }
      }
    }
  }

   public Dimension getPreferredSize()
   {
     if (tileset == null)
     {
       return new Dimension(10, 100);
     }
     
     int size = tileset.size();
     int height = (size / tilesPerRow) + 1;
     
     return new Dimension(tilesPerRow * twidth, height * theight);
   }

  // MouseInputListener interface
  public void mouseExited(MouseEvent e)
  {
  }

  public void mouseClicked(MouseEvent e)
  {
    Point point = getMousePosition();
    refreshSelectedTiles(new Rectangle(point.x,point.y, 1,1));
    fireTileSelectionEvent(selectedTiles,selectedBrush);
    repaint();
  }

  public void mouseEntered(MouseEvent e)
  {
  }

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1)
    {
      doDrag(e);
    }
    
    repaint();
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1)
    {
      finishDrag(e);
    }
  }

  public void mouseDragged(MouseEvent e)
  {
    doDrag(e);
  }

  public void mouseMoved(MouseEvent e)
  {
    finishDrag(e);
  }
  
  /** starts the drag operation */
  private void doDrag(MouseEvent e)
  {
    Point p = getMousePosition();
    if (p == null)
      return;

    if (dragInProgress)
    {
      currentDragPoint = p;
    }
    else
    {
      dragInProgress = true;
      dragStartPoint = getMousePosition();
      currentDragPoint = dragStartPoint;
    }
    refreshSelectedTiles(getRectangle(dragStartPoint, currentDragPoint));
    fireTileSelectionEvent(selectedTiles,selectedBrush);
    repaint();
  }
  
  /** finishes the drag operation */
  private void finishDrag(MouseEvent e)
  {
    if (dragInProgress)
    {
      refreshSelectedTiles(getRectangle(dragStartPoint, currentDragPoint));
    }
    dragInProgress = false;
    repaint();
  }

  /** selects all tiles within the selection rectangle */
  private void refreshSelectedTiles(Rectangle rect)
  {
    List<Tile> tileList = new ArrayList<Tile>();
    
    MultiTileBrush brush = new MultiTileBrush();
    
    int twidth = tileset.getStandardWidth() + 1;
    int theight = tileset.getTileHeightMax() + 1;
    
    int maxx = rect.x + rect.width;
    maxx += (maxx % twidth > 0) ? twidth - (maxx % twidth) : 0;
    int maxy = rect.y + rect.height;
    maxy += (maxy % theight > 0) ? theight - (maxy % theight) : 0;
    

    for (int x = rect.x, brushx = 0; x < maxx ; x += twidth, brushx++)
    {
      for (int y = rect.y, brushy = 0; y < maxy ; y += theight, brushy++)
      {
        Tile tile = getTileAtPoint(x,y);
        if (tile != null)
        {
          tileList.add(tile);
          brush.addTile(brushx,brushy,tile);
        }
      }
    }
    selectedTiles = tileList;
    selectedBrush = brush;
  }
  
}
