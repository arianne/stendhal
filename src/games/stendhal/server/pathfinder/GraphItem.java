/*
 * GraphItem.java
 *
 * Created on 22 de julio de 2002, 0:27
 */
package games.stendhal.server.pathfinder;

import java.util.*;

/**
 * @author Ramon Talavera: maxdemian@terra.es
 * @version
 */
public class GraphItem implements Graphable
{
  /** Brother's list */
  public List<GraphItem>      adjList       = new ArrayList<GraphItem>();
  /** Unique identifier in Graph */
  public int                  IDinGraph;                   
  public int                  dijkstraindex = -1;
  /** Extra info carried by the item */
  private Object              info;                        
  /** coordinates */
  private int                 x, y;
  /** distance from the origin to this item */
  private int                 distance      = -1;

  /** dijkstra's parent */
  private GraphItem parent; 

  /** Creates new GraphItem */
  public GraphItem()
  {
    setIDInGraph(-1);
  }

  public GraphItem getParent()
  {
    return parent;
  }

  public void setParent(GraphItem p)
  {
    parent = p;
  }

  public int getDistance()
  {
    return distance;
  }

  public void setDistance(int d)
  {
    distance = d;
  }

  public void setIDInGraph(int idx)
  {
    this.IDinGraph = idx;
  }

  public int getIDInGraph()
  {
    return this.IDinGraph;
  }

  public List<GraphItem> getAdjList()
  {
    return this.adjList;
  }

  public int getX()
  {
    return x;
  }

  public void setX(int x)
  {
    this.x = x;
  }

  public int getY()
  {
    return y;
  }

  public void setY(int y)
  {
    this.y = y;
  }

  public void setInfo(Object Obj)
  {
    info = Obj;
  }

  public Object getInfo()
  {
    return info;
  }
}
