/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * Exits the application
 * @author mtotz
 */
public class ExitApplicationAction extends AbstractAction
{
  private static final long serialVersionUID = -8126594339805319303L;

  private MapEditor mapEditor;
  
  public ExitApplicationAction(MapEditor mapEditor)
  {
    super("Exit");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q"));
    putValue(SHORT_DESCRIPTION, "Exit the map editor");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    mapEditor.exit();
  }

}
