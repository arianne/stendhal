/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.core.TileSet;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.dialog.NewTilesetDialog;

/**
 * @author mtotz
 *
 */
public class NewTilesetAction extends AbstractAction
{
  private static final long serialVersionUID = -8888155680402257585L;

  private MapEditor mapEditor;
  
  public NewTilesetAction(MapEditor mapEditor)
  {
    super("New Tileset...");
    putValue(SHORT_DESCRIPTION, "Add a new internal tileset");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (mapEditor.currentMap != null)
    {
      NewTilesetDialog dialog = new NewTilesetDialog(mapEditor.appFrame, mapEditor.currentMap);
      TileSet newSet = dialog.create();
      if (newSet != null)
      {
        mapEditor.currentMap.addTileset(newSet);
      }
    }
  }
}
