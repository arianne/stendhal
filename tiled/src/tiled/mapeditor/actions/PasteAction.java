/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.core.MapLayer;
import tiled.mapeditor.MapEditor;

public class PasteAction extends AbstractAction
{
  private static final long serialVersionUID = -9094834729794547379L;

  private MapEditor mapEditor;
  
  public PasteAction(MapEditor mapEditor)
  {
    super("Paste");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
    putValue(SHORT_DESCRIPTION, "Paste");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (mapEditor.currentMap != null && mapEditor.clipboardLayer != null)
    {
//      List<MapLayer> layersBefore = mapEditor.currentMap.getLayerList();
//      MapLayer ml = mapEditor.createLayerCopy(mapEditor.clipboardLayer);
//      ml.setName("Layer " + mapEditor.currentMap.getTotalLayers());
//      mapEditor.currentMap.addLayer(ml);
//      mapEditor.undoSupport.postEdit(new MapLayerStateEdit(mapEditor.currentMap, layersBefore,mapEditor.currentMap.getLayerList(), "Paste Selection"));
    }
  }
}