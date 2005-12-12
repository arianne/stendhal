/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.mapeditor.MapEditor;

/**
 * @author mtotz
 *
 */
public class DelLayerAction extends AbstractAction
{
  private static final long serialVersionUID = -6854510334788892219L;

  private MapEditor mapEditor;
  
  public DelLayerAction(MapEditor mapEditor)
  {
    super("Delete Layer");
    putValue(SHORT_DESCRIPTION, "Delete current layer");
    putValue(SMALL_ICON,MapEditor.loadIcon("resources/gnome-delete.png"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (mapEditor.currentLayer >= 0)
    {
      mapEditor.currentMap.removeLayer(mapEditor.currentLayer);
      mapEditor.setCurrentLayer(mapEditor.currentLayer < 0 ? 0 : mapEditor.currentLayer);
    }
  }
}
