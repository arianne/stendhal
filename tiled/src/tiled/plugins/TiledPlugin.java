/*
 * 
 */
package tiled.plugins;

import java.util.List;

/** The mother of plugins for tiled */
public interface TiledPlugin
{
  /** returns the description of the plugin */
  String getPluginDescription();
  /** 
   * Sets the list where the plugin can store all messages it wants to tell
   * the user
   */
  void setMessageList(List<String> errorList);
}
