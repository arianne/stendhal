/*
 * PathState.java
 *
 * Created on 5. August 2005, 20:48
 */

package games.stendhal.server;

/**
 * Some states the pathfinder may return.
 *
 * @author Matthias Totz
 */
public enum PathState
{
  /** Found a path */
  PATH_FOUND,
  /** No Path available */
  PATH_NOT_FOUND,
  /** The pathfinder took too long finding a path so it stopped. Maybe partial
   * path is available, but that is not guaranteed. */
  TIMEOUT_ON_SEARCH,
  /** The pathfinder queue is at its limit. Try again later or reduce the
   * general pathfinder load. */
  NO_RESOURCES;
  
}
