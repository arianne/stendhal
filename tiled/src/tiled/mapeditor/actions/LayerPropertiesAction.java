/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.core.MapLayer;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.dialog.PropertiesDialog;

/**
 * @author mtotz
 *
 */
public class LayerPropertiesAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;

  private MapEditor mapEditor;
  
  public LayerPropertiesAction (MapEditor mapEditor)
  {
    super("Layer Properties");
    putValue(SHORT_DESCRIPTION, "Current layer properties");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    MapLayer layer = mapEditor.getCurrentLayer();
    PropertiesDialog lpd = new PropertiesDialog(mapEditor.appFrame, layer.getProperties());
    lpd.setTitle(layer.getName() + " Properties");
    lpd.getProps();
  }
}
