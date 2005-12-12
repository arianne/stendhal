/**
 * 
 */
package tiled.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;

/**
 * @author mtotz
 */
public class Orthogonal extends MapView
{
  /** minimap tile size is 2 pixel */
  private static final int MINIMAP_TILE_SIZE = 2;
  
  /** retuns the tile size depending on the given zoom level */
  private Dimension getTileSize(double zoom)
  {
    return new Dimension((int)(map.getTileWidth() * zoom),
                         (int)(map.getTileHeight() * zoom));
  }

  /** size of the map */
  public Dimension getSize()
  {
    if (map == null) 
    {
      return new Dimension(1000, 1000);
    }

    Dimension dim = getTileSize(zoom);
    
    dim.width  *= map.getWidth();
    dim.height *= map.getHeight();
    return dim;
  }

  /**
   * (re)draws a portion of the map. Note that clipArea is in Tile coordinate
   * space, not pixel space. The destination is always the upper left
   * corner(0,0) of g.
   * 
   * @param g
   *          the graphic to draw to
   * @param clipArea
   *          the are to draw in tile coordinates
   */
  public void draw(Graphics g, Rectangle clipArea)
  {
    draw(g,clipArea,zoom);
  }
  
  /** draws the map with the given zoom level */
  private void draw(Graphics g, Rectangle clipArea, double zoom)
  {
    if (map == null)
      return;
    
    // draw each tile layer
    for (MapLayer layer : map.getLayerList())
    {
      if (layer instanceof TileLayer && layer.isVisible())
      {
        TileLayer tileLayer = (TileLayer) layer;
        paintLayer(g,tileLayer,clipArea,zoom);
      }
    }
  }

  /** paints the specified region of the layer */
  protected void paintLayer(Graphics g, TileLayer layer, Rectangle clipArea, double zoom)
  {
    setLayerOpacity(g,layer);
    
    
    // Determine tile size and offset
    Dimension tsize = getTileSize(zoom);
    if (tsize.width <= 0 || tsize.height <= 0)
      return;
    
    g.setColor(Color.BLACK);
    

    int toffset = 0;

    int startX = clipArea.x;
    int startY = clipArea.y;
    int endX   = clipArea.x + clipArea.width;
    int endY   = clipArea.y + clipArea.height;

    // Draw this map layer
    for (int y = startY, gy = startY * tsize.height + toffset; y < endY; y++, gy += tsize.height)
    {
      for (int x = startX, gx = startX * tsize.width + toffset; x < endX; x++, gx += tsize.width)
      {
        Tile tile = layer.getTileAt(x, y);

        if (tile != null && tile != map.getNullTile())
        {
          tile.draw(g, gx, gy, zoom);
        }
      }
    }
  }


  /** 
   * converts the screen position to tile position.
   * 
   * @param screenCoords tile coords
   * @return screen coords (upper left corner of the tile)
   */
  public Point screenToTileCoords(Point screenCoords)
  {
    Dimension tsize = getTileSize(zoom);
    Point p = new Point(screenCoords.x / tsize.width, screenCoords.y / tsize.height);
    if (p.x > map.getWidth())
    {
      p.x = map.getWidth();
    }
    if (p.y > map.getHeight())
    {
      p.y = map.getHeight();
    }
    
    return p;
  }

  /**
   * converts the tile position to screen position.
   * 
   * @param tileCoords screen coords
   * @return tile coords
   */
  public Point tileToScreenCoords(Point tileCoords)
  {
    Dimension tsize = getTileSize(zoom);
    return new Point(tileCoords.x * tsize.width, tileCoords.y * tsize.height);
  }

  /** returns a minimap */
  protected BufferedImage prepareMinimapImage()
  {
    int width = (int) (map.getWidth() * MINIMAP_TILE_SIZE);
    int height = (int) (map.getWidth() * MINIMAP_TILE_SIZE);
    
    
    GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    BufferedImage image = config.createCompatibleImage(width,height);
    
    Graphics g = image.createGraphics();
    Rectangle all = new Rectangle(0,0,map.getWidth(),map.getHeight());

    draw(g,all,getMinimapScale());
    
    return image;
  }
  
  /** returns the minimap zoom level. */
  public double getMinimapScale()
  {
    return (1.0 / (map.getTileWidth() / MINIMAP_TILE_SIZE));
  }

  /** updates the minimap */
  public void updateMinimapImage(Rectangle modifiedRegion)
  {
    if (minimapImage == null)
    {
      // minimap not prepared yet
      return;
    }

    Graphics minimapGraphics = minimapImage.createGraphics();
    draw(minimapGraphics,modifiedRegion,getMinimapScale());
    
  }

  /**
   * Retuns list of tiles that lies in the given rectangle
   * @param rect the rectangle (in tile coordinate space)
   * @return list of tiles in the rectangle
   */
  public List<Point> getSelectedTiles(Rectangle rect,int layer)
  {
    List<Point> list = new ArrayList<Point>();

    MapLayer mapLayer = map.getLayer(layer);
    if (!(mapLayer instanceof TileLayer))
    {
      return list;
    }
    
    TileLayer tileLayer = (TileLayer) mapLayer;
    
    Dimension tsize = getTileSize(zoom);
    Point p1 = rect.getLocation();
    Point p2 = rect.getLocation();
    p2.translate(rect.width,rect.height);
    
    if (p2.x % tsize.width > 0)
    {
      p2.x += tsize.width;
    }
    if (p2.y % tsize.height > 0)
    {
      p2.y += tsize.height;
    }
    
    p1 = screenToTileCoords(p1);
    p2 = screenToTileCoords(p2);
    

    // Draw this map layer
    for (int y = p1.y; y < p2.y; y++)
    {
      for (int x = p1.x; x < p2.x; x++)
      {
        Tile tile = tileLayer.getTileAt(x, y);

        if (tile != null && tile != map.getNullTile())
        {
          list.add(new Point(x,y));
        }
      }
    }
    
    return list;
  }

  /**
   * draws a frame around the tile.
   * @param g graphics context
   * @param tile the tile
   */
  public void drawTileHighlight(Graphics g, Point tile)
  {
    Point p = tileToScreenCoords(tile);
    Dimension tsize = getTileSize(zoom);
    g.drawRect(p.x, p.y, tsize.width, tsize.height);
  }
  
  

}
