/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class ZoomOutAction extends AbstractAction
{
  private static final long serialVersionUID = 4963537857700059134L;

  private MapEditor mapEditor;

  public ZoomOutAction(MapEditor mapEditor)
  {
    super("Zoom Out");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control MINUS"));
    putValue(SHORT_DESCRIPTION, "Zoom out one level");
    putValue(SMALL_ICON, MapEditor.loadIcon("resources/gnome-zoom-out.png"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (mapEditor.currentMap != null)
    {
      mapEditor.mapView.zoomOut();
      mapEditor.mapEditPanel.repaint();
    }
  }
}