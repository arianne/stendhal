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
public class AddLayerAction extends AbstractAction
{
  private static final long serialVersionUID = -9136823833761490842L;

  private MapEditor mapEditor;
  
  public AddLayerAction(MapEditor mapEditor)
  {
    super("Add Layer");
    putValue(SHORT_DESCRIPTION, "Add a Layer");
    putValue(SMALL_ICON,MapEditor.loadIcon("resources/gnome-new.png"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    mapEditor.currentMap.addLayer();
    mapEditor.setCurrentLayer(mapEditor.currentMap.getTotalLayers() - 1);
  }

}
