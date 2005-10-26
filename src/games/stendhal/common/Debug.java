/*
 * Debug.java
 *
 * Created on 12. Oktober 2005, 21:28
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package games.stendhal.common;

/**
 * Gathers all Debug constants in one place
 * @author mtotz
 */
public class Debug
{
  /** if this is enable tileset are loaded on demand. */
  public static final boolean VERY_FAST_CLIENT_START = true;

  /** should the minimap be drawn? Note: This slows down performance */
  public static final boolean DRAW_MINIMAP = true;
  
  /** enables cycling through the panel textures by clicking at the title bar */
  public static final boolean CYCLE_PANEL_TEXTURES = true;
  
  /** should the creature ai and pathfinding be shown? Note: The server must
   * send these infos (CREATRUES_DEBUG_SERVER) */
  public static final boolean CREATRUES_DEBUG_CLIENT = false;
  
  /** should the server send debug information about creature ai and pathfinding
   * to the client? Note: CREATRUES_DEBUG_CLIENT should be enabled too */
  public static final boolean CREATRUES_DEBUG_SERVER = false;
  
  
  /** no instance */
  private Debug()
  {}
  
}
