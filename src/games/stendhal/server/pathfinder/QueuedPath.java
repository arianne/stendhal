/*
 * QueuedPath.java
 *
 * Created on 5. August 2005, 20:43
 *
 */

package games.stendhal.server.pathfinder;

import games.stendhal.server.entity.Entity;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * A path queued for calculation. Do not even try to modify this object when it
 * is in the queue waiting to be processed.
 *
 * @author Matthias Totz
 */
public final class QueuedPath
{
  private PathListener listener;
  private Entity entity;
  private int x;
  private int y;
  private Rectangle2D destination;
  private double maxPathRadius;
  private List<Path.Node> path;
  private boolean canceled;

  /** Creates a new instance of QueuedPath */
  public QueuedPath(PathListener listener, Entity entity, int x, int y, Rectangle2D destination, double maxPathRadius)
  {
    this.listener      = listener;
    this.entity        = entity;
    this.x             = x;
    this.y             = y;
    this.destination   = destination;
    this.maxPathRadius = maxPathRadius;
    this.canceled      = false;
    this.path          = null;
  }
  /** sets the path */
  public void setPath(List<Path.Node> path)
  {
    this.path = path;
  }
  
  /** returns the path */
  public List<Path.Node> getPath()
  {
    return path;
  }
  
  /** returns start x */
  public int getX()
  {
    return x;
  }
  
  /** returns start y */
  public int getY()
  {
    return y;
  }
  
  /** returns the destination arae */
  public Rectangle2D getDestination()
  {
    return destination;
  }
  
  /** returns the entity */
  public Entity getEntity()
  {
    return entity;
  }
  
  /** returns the listener*/
  public PathListener getListener()
  {
    return listener;
  }
  
  /** Cancels this path request. You cannot revert the cancelation. */
  public void cancel()
  {
    canceled = true;
  }
  
  /** returns true if this path request is canceled, else false. */
  public boolean isCanceled()
  {
    return canceled;
  }

  /** returns the maximum radius a path is searched (negative if unlimited))*/
  public double getMaxPathRadius()
  {
    return maxPathRadius;
  }
  
}

