/**
 * 
 */
package tiled.core;

import java.awt.geom.Area;
import java.util.Properties;

/**
 * The PropertiesLayer contains Property-Objects for each position. You need
 * only one per map. 
 * 
 * @author mtotz
 *
 */
public class PropertiesLayer extends MapLayer
{
  private Properties[][] properties;
  
  /**
   * @param width
   * @param height
   */
  public PropertiesLayer(int width, int height)
  {
    super(width,height);
    properties = new Properties[width][height];
    setName("properties");
  }

  /** 
   * Returns the properties at x,y. Returns null when x,y is not inside the
   * layer.
   * <b>Note:</b> This returns the actual <code>Properties</code> object, so any
   * changes to the returned object are reflected immediatly in the map. Also
   * note that the object itself is synchonized (it is a <code>Hashtable</code>), but
   * concurrent access may result in a <code>CuncurrentModificationException</code>.  
   */
  public Properties getProps(int x,int y)
  {
    if (!contains(x,y))
      return null;

    Properties props = properties[x][y];
    if (props == null)
    {
      props = new Properties();
      properties[x][y] = props;
    }
    return props;
  }
  
  /** returns the properties at x,y. Never returns null */
  public void setProps(int x,int y, Properties properties)
  {
    if (!contains(x,y))
      return ;
    Properties props;
    props = new Properties();
    
    if (properties != null)
    {
      // copy properties list
      props.putAll(properties);
    }
    this.properties[x][y] = props;
  }

  public void rotate(int angle)   { }
  public void mirror(int dir)  {  }
  public void mergeOnto(MapLayer other)  {  }
  public void copyFrom(MapLayer other)  {  }
  public void maskedCopyFrom(MapLayer other, Area mask)  {  }
  public MapLayer createDiff(MapLayer ml)  {    return null; }
  public void copyTo(MapLayer other)  {  }
  public void resize(int width, int height, int dx, int dy)  {  }
  public boolean isUsed(Tile t)  {  return false;  }

}
