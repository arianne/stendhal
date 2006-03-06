/*
 * Graph.java
 *
 * Created on 18 de julio de 2002, 16:39
 */
package games.stendhal.server.pathfinder;

import java.util.*;

/**
 * @author Ramon Talavera maxdemian@terra.es
 * @version
 */
public class Graph
{

  public Vector<GraphItem> objList   = null;     // A graph is a GraphItems Container
  public int    unique_id = 0;
  private int[] lastDijkstraParents;
  private int[] lastDijkstraDistances;

  /** Creates new Graph */
  public Graph()
  {
    objList = new Vector<GraphItem>();
  }

  public boolean isEmpty()
  {
    if (objList.size() == 0)
      return true;
    return false;
  }

  public int nPoints()
  {
    return objList.size();
  }

  public boolean contains(Object Obj)
  {
    if (getItem(Obj) != null)
      return true;

    return false;
  }

  public Object getObject(int IDNum)
  {
    if (IDNum == -1)
      return null;

    for (int i = 0; i < objList.size(); i++)
    {
      GraphItem itm = objList.get(i);

      if (itm != null && itm.getIDInGraph() == IDNum)
        return itm.getInfo();
    }

    return null;
  }

  public GraphItem getItem(Object Obj)
  {
    int i;
    if (Obj == null)
      return null;

    for (i = 0; i < objList.size(); i++)
    {
      GraphItem itm = objList.get(i);
      if (itm != null && itm.getInfo() == Obj)
        return itm;

    }
    return null;
  }

  public GraphItem getItem(int i)
  {
    if (i > objList.size())
      return null;

    return  objList.get(i);
  }

  public GraphItem addElement(Object Obj, int x, int y)
  {
    GraphItem itm;
    itm = getItem(Obj);
    
    // First make sure that it does not exist in the object list already
    if (itm != null)
    {
      return itm;
    }

    itm = new GraphItem();
    itm.setInfo(Obj);
    itm.setX(x);
    itm.setY(y);
    int nidx = 0;

    nidx = unique_id++;

    itm.setIDInGraph(nidx);
    objList.add(itm);
    return itm;
  }

  public List<GraphItem> getAdjList(GraphItem item)
  {
    return item.getAdjList();
  }

  public List<GraphItem> getAdjListFor(int itemID)
  {
    for (GraphItem itm : objList)
    {
      if (itm.getIDInGraph() == itemID)
        return itm.getAdjList();
    }

    return null;
  }

  public int getNeighbours(Object obj)
  {
    GraphItem itm = getItem(obj);
    return getNeighbours(itm);
  }

  public int getNeighbours(GraphItem item)
  {
    if (item == null)
      return -1;

    List<GraphItem>  list = getAdjList(item);
    if (list == null)
      return -1;

    return list.size();
  }

  public boolean deleteElement(Object obj)
  {
    return deleteNode(obj);
  }

  public boolean deleteNode(Object obj)
  {
    GraphItem item = getItem(obj);
    
    if (item == null)
      return true;

    // Look in all adjlist for ocurrences of this node
    // Remove ALL connections reaching this node
    for (int i = 0; i < objList.size(); i++)
    {
      GraphItem currentItem = objList.get(i);

      if (currentItem != null)
        currentItem.getAdjList().remove(item);
    }

    this.objList.remove(item);
    item.setIDInGraph(-1); // Not in Graph;
    return true;

  }

  public boolean addBiConnection(Object I, Object J)
  {
    if (addConnection(I, J) == false)
      return false;

    return addConnection(J, I);
  }

  public boolean addConnection(Object I, Object J)
  {
    GraphItem itemI, itemJ;
    itemI = getItem(I);
    itemJ = getItem(J);

    if ((itemI == null) || (itemJ == null))
      return false;

    // find in the Objlist the Item with Graph Index i
    // and locate its corresponding adjacency list
    List<GraphItem>  list = itemI.getAdjList();

    if (list == null)
    {
      return false;
    }

    // Element J is added to i's adjacency list
    if (list.contains(itemJ) == false)
    {
      list.add(itemJ);
    }

    return true;

  }

  public int[] getLastDijkstraParents()
  {
    return lastDijkstraParents;
  }

  public int[] getLastDijkstraDistances()
  {
    return lastDijkstraDistances;
  }

  public int[] dijkstra(GraphItem start)
  {
    LinkedList<GraphItem> queue = new LinkedList<GraphItem>();

    int d[] = new int[objList.size()];
    int p[] = new int[objList.size()];

    boolean v[] = new boolean[objList.size()];

    for (int i = 0; i < objList.size(); i++)
    {
      GraphItem itm = objList.get(i);
      itm.dijkstraindex = i;
    }

    GraphItem fuente = start;
    if (objList.contains(start) == false)
      return null;

    if (start == null)
      return null;

    for (int i = 0; i < objList.size(); i++)
    {
      d[i] = 32000; // change this to a higher ammount if needed
      p[i] = -1;
      v[i] = false;
    }

    d[fuente.dijkstraindex] = 0;
    queue.addFirst(fuente);

    while (queue.isEmpty() == false)
    {
      GraphItem w = queue.removeLast();

      if (v[w.dijkstraindex] == false)
      {
        v[w.dijkstraindex] = true;
        List<GraphItem> list = this.getAdjList(w);

        for (int j = 0; j < list.size(); j++)
        {
          GraphItem z = list.get(j);

          if (z != null && d[z.dijkstraindex] > d[w.dijkstraindex] + 1)
          {
            d[z.dijkstraindex] = d[w.dijkstraindex] + 1;
            p[z.dijkstraindex] = w.dijkstraindex;
            z.setParent(w);
            z.setDistance(d[w.dijkstraindex] + 1);
            queue.addFirst(z);
          }
        }
      }
    }

    lastDijkstraParents = p;
    lastDijkstraDistances = d;
    return p;
  }

  public int getNumBrothers(Object obj)
  {
    if (obj == null)
      return -1;

    GraphItem item = this.getItem(obj);

    if (item == null)
      return 0;

    return (this.getAdjList(item).size());
  }

  public static void main(String[] args)
  {
    System.out.println("SHORTEST PATH USING Dijkstra's ALGORITHM : July 2002 ,Ramon Talavera: maxdemian@terra.es");
    System.out.println("Use at your own risk :)");

    Graph my_graph = new Graph();
    Object A = new Object();
    Object B = new Object();
    Object C = new Object();

    my_graph.addElement(A, 100, 100);
    my_graph.addElement(B, 100, 150);
    my_graph.addElement(C, 150, 100);
    my_graph.addConnection(A, B);
    my_graph.addConnection(B, C);

    // my_graph.addConnection(A,C);

    // Parte del item que contiene A y busca caminos más cortos
    int d[] = my_graph.dijkstra(my_graph.getItem(A));

    System.out.println("Distance to the origin: (node number,distance)");
    for (int i = 0; i < d.length; i++)
    {
      System.out.print("(" + i + "," + d[i] + "),");
    }

    System.out.println("\nSHORTEST PATH:");
    System.out.println("Parent's list to follow backwards to get the shortest path to the origin:");
    System.out.println("(node,parent_of_node)");

    for (int i = 0; i < my_graph.getLastDijkstraParents().length; i++)
    {
      System.out.print("(" + i + "," + my_graph.getLastDijkstraParents()[i] + "),");
    }
  }
}