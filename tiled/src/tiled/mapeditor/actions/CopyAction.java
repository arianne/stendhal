/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.core.ObjectGroup;
import tiled.core.TileLayer;
import tiled.mapeditor.MapEditor;

public class CopyAction extends AbstractAction
{
  private static final long serialVersionUID = -7093838522430390018L;

  private MapEditor mapEditor;
  
  public CopyAction(MapEditor mapEditor)
  {
    super("Copy");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
    putValue(SHORT_DESCRIPTION, "Copy");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (mapEditor.currentMap != null && mapEditor.marqueeSelection != null)
    {
      if (mapEditor.getCurrentLayer() instanceof TileLayer)
      {
        mapEditor.clipboardLayer = new TileLayer(mapEditor.marqueeSelection.getSelectedAreaBounds());
      } else if (mapEditor.getCurrentLayer() instanceof ObjectGroup)
      {
        mapEditor.clipboardLayer = new ObjectGroup(mapEditor.marqueeSelection.getSelectedAreaBounds());
      }
      mapEditor.clipboardLayer.maskedCopyFrom(mapEditor.getCurrentLayer(), mapEditor.marqueeSelection.getSelectedArea());
    }
  }
}