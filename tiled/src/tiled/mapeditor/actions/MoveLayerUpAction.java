/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * @author mtotz
 *
 */
public class MoveLayerUpAction extends AbstractAction
{
  private static final long serialVersionUID = -6847888144371163825L;

  private MapEditor mapEditor;
  
  public MoveLayerUpAction(MapEditor mapEditor)
  {
    super("Move Layer Up");
    putValue(SHORT_DESCRIPTION, "Move layer up one in layer stack");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift PAGE_UP"));
    putValue(SMALL_ICON,MapEditor.loadIcon("resources/gnome-up.png"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (mapEditor.currentLayer >= 0)
    {
      try
      {
        mapEditor.currentMap.swapLayerUp(mapEditor.currentLayer);
        mapEditor.setCurrentLayer(mapEditor.currentLayer + 1);
      } catch (Exception ex)
      {
        System.out.println(ex.toString());
      }
    }
  }
}
