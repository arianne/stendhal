/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class ZoomInAction extends AbstractAction
{
  private static final long serialVersionUID = -5253002744432344462L;
  
  private MapEditor mapEditor;

  public ZoomInAction(MapEditor mapEditor)
  {
    super("Zoom In");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
    putValue(SHORT_DESCRIPTION, "Zoom in one level");
    putValue(SMALL_ICON, MapEditor.loadIcon("resources/gnome-zoom-in.png"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (mapEditor.currentMap != null)
    {
      mapEditor.mapView.zoomIn();
      mapEditor.statusBar.setZoom(mapEditor.mapView.getScale());
      mapEditor.mapEditPanel.repaint();
    }
  }
}