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
public class CreateMultiLayerBrushAction extends AbstractAction
{
  private static final long serialVersionUID = -8004754730959503398L;

  private MapEditor mapEditor;
  
  public CreateMultiLayerBrushAction(MapEditor mapEditor)
  {
    super("New ext. brush");
    putValue(SHORT_DESCRIPTION, "Creates a new multilayer brush");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    mapEditor.openMap();
  }

}
