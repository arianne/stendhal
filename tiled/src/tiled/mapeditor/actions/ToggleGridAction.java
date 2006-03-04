/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * @author mtotz
 *
 */
public class ToggleGridAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;

  private MapEditor mapEditor;
  
  public ToggleGridAction(MapEditor mapEditor)
  {
    super("Show Grid");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control G"));
    putValue(SHORT_DESCRIPTION, "Toggle grid");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
    if (item != null)
    {
      mapEditor.mapView.setPadding(item.isSelected() ? 1 : 0);
      mapEditor.mapEditPanel.revalidate();
    }
  }

}
