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
public class MoveLayerDownAction extends AbstractAction
{
  private static final long serialVersionUID = -2972885439985659250L;

  private MapEditor mapEditor;
  
  public MoveLayerDownAction(MapEditor mapEditor)
  {
    super("Move Layer Down");
    putValue(SHORT_DESCRIPTION, "Move Layer Down");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift PAGE_DOWN"));
    putValue(SMALL_ICON,MapEditor.loadIcon("resources/gnome-down.png"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (mapEditor.currentLayer >= 0)
    {
      try
      {
        mapEditor.currentMap.swapLayerDown(mapEditor.currentLayer);
        mapEditor.setCurrentLayer(mapEditor.currentLayer - 1);
      } catch (Exception ex)
      {
        System.out.println(ex.toString());
      }
    }
  }
}
