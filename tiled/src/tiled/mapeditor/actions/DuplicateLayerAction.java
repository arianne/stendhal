/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.core.MapLayer;
import tiled.mapeditor.MapEditor;

/**
 * @author mtotz
 *
 */
public class DuplicateLayerAction extends AbstractAction
{
  private static final long serialVersionUID = -3670111021863463422L;

  private MapEditor mapEditor;
  
  public DuplicateLayerAction(MapEditor mapEditor)
  {
    super("Duplicate Layer");
    putValue(SHORT_DESCRIPTION, "Duplicate current layer");
    putValue(SMALL_ICON,MapEditor.loadIcon("resources/gimp-duplicate-16.png"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (mapEditor.currentLayer >= 0)
    {
      try
      {
        MapLayer clone = (MapLayer) mapEditor.getCurrentLayer().clone();
        clone.setName(clone.getName() + " copy");
        mapEditor.currentMap.addLayer(clone);
      } catch (CloneNotSupportedException ex)
      {
        ex.printStackTrace();
      }
      mapEditor.setCurrentLayer(mapEditor.currentMap.getTotalLayers() - 1);
    }
  }
}
