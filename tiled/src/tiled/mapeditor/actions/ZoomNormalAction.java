/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class ZoomNormalAction extends AbstractAction
{
  private static final long serialVersionUID = 4808296576226530709L;

  private MapEditor mapEditor;

  public ZoomNormalAction(MapEditor mapEditor)
  {
    super("Zoom Normalsize");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 1"));
    putValue(SHORT_DESCRIPTION, "Zoom 100%");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (mapEditor.currentMap != null)
    {
      mapEditor.mapView.zoomNormalize();
      mapEditor.statusBar.setZoom(mapEditor.mapView.getScale());
      mapEditor.mapEditPanel.repaint();
    }
  }
}