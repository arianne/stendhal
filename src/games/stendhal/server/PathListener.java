/*
 * PathListener.java
 *
 * Created on 5. August 2005, 20:44
 *
 */

package games.stendhal.server;

/**
 * This interface must be implemented by all classes that needs to calculate 
 * path asynchonous from the main thread.
 *
 * @author Matthias Totz
 */
public interface PathListener
{
  /**
   * Returns when the pathfinder is finished. Do <b>not</b> do some wild things
   * in this method as it runs in the pathfinder thread. Just save the path and
   * the state and return. All other stuff can be done in the <code>logic()</code> method.
   *
   * @param path the path. it might me no or a partial path depending on the 
   *             state.
   * @param state the state of the path.
   * @see PathState
   */
  void onPathFinished(QueuedPath path, PathState state);
}
