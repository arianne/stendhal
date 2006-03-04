/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.dialog.TilesetManager;

/**
 * Opens the tileset manager
 * @author mtotz
 */
public class TilesetManagerAction extends AbstractAction
{
  private static final long serialVersionUID = -4785910554628085157L;

  private MapEditor mapEditor;
  
  public TilesetManagerAction(MapEditor mapEditor)
  {
    super("Tileset Manager");
    putValue(SHORT_DESCRIPTION, "Open the tileset manager");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (mapEditor.currentMap != null)
    {
      TilesetManager manager = new TilesetManager(mapEditor.appFrame, mapEditor.currentMap);
      manager.setVisible(true);
    }
  }

}
