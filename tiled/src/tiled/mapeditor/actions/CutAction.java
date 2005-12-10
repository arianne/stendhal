/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Area;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.core.MapLayer;
import tiled.core.ObjectGroup;
import tiled.core.TileLayer;
import tiled.mapeditor.MapEditor;

public class CutAction extends AbstractAction
{
  private static final long serialVersionUID = -244183316986816427L;

  private MapEditor mapEditor;
  
  public CutAction(MapEditor mapEditor)
  {
    super("Cut");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
    putValue(SHORT_DESCRIPTION, "Cut");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (mapEditor.currentMap != null && mapEditor.marqueeSelection != null)
    {
      MapLayer ml = mapEditor.getCurrentLayer();

      if (mapEditor.getCurrentLayer() instanceof TileLayer)
      {
        mapEditor.clipboardLayer = new TileLayer(mapEditor.marqueeSelection.getSelectedAreaBounds());
      } else if (mapEditor.getCurrentLayer() instanceof ObjectGroup)
      {
        mapEditor.clipboardLayer = new ObjectGroup(mapEditor.marqueeSelection.getSelectedAreaBounds());
      }
      mapEditor.clipboardLayer.maskedCopyFrom(ml, mapEditor.marqueeSelection.getSelectedArea());

      Rectangle area = mapEditor.marqueeSelection.getSelectedAreaBounds();
      Area mask = mapEditor.marqueeSelection.getSelectedArea();
      if (ml instanceof TileLayer)
      {
        TileLayer tl = (TileLayer) ml;
        for (int i = area.y; i < area.height + area.y; i++)
        {
          for (int j = area.x; j < area.width + area.x; j++)
          {
            if (mask.contains(j, i))
            {
              tl.setTileAt(j, i, mapEditor.currentMap.getNullTile());
            }
          }
        }
      }
      // mapView.repaintRegion(area);
    }
  }
}