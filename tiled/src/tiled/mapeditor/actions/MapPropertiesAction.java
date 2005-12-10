/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.dialog.PropertiesDialog;

/**
 * Shows the map properties
 * @author mtotz
 */
public class MapPropertiesAction extends AbstractAction
{
  private static final long serialVersionUID = 248911712933902868L;

  private MapEditor mapEditor;
  
  public MapPropertiesAction (MapEditor mapEditor)
  {
    super("Properties");
    putValue(SHORT_DESCRIPTION, "Map properties");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    PropertiesDialog pd = new PropertiesDialog(mapEditor.appFrame, mapEditor.currentMap.getProperties());
    pd.setTitle("Map Properties");
    pd.getProps();
  }

}
