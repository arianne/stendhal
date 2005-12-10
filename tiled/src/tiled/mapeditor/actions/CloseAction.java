/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * Closes the current map
 * @author mtotz
 */
public class CloseAction extends AbstractAction
{
  private static final long serialVersionUID = -1502825220028094997L;

  private MapEditor mapEditor;
  
  public CloseAction(MapEditor mapEditor)
  {
    super("Close");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
    putValue(SHORT_DESCRIPTION, "Close this map");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    mapEditor.closeMap();
  }

}
