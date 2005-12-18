/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.StatefulTile;
import tiled.core.Tile;
import tiled.core.TileGroup;
import tiled.core.TileLayer;
import tiled.mapeditor.MapEditor;

/**
 * @author mtotz
 *
 */
public class CreateMultiLayerBrushAction extends AbstractAction
{
  private static final long serialVersionUID = -8004754730959503398L;

  private MapEditor mapEditor;
  
  public CreateMultiLayerBrushAction(MapEditor mapEditor)
  {
    super("New ext. brush");
    putValue(SHORT_DESCRIPTION, "Creates a new multilayer brush using all vivible layers");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    List<Point> selectedList = new ArrayList<Point>(mapEditor.getSelectedTiles());
    List<StatefulTile> brushList = new ArrayList<StatefulTile>();
    
    Map map = mapEditor.currentMap;

    // get all layers
    for (int i = 0; i < map.getTotalLayers(); i++)
    {
      MapLayer mapLayer = map.getLayer(i);
      if (mapLayer.isVisible() && mapLayer instanceof TileLayer)
      {
        TileLayer tileLayer = (TileLayer) mapLayer;
        // copy tiles
        for (Point p : selectedList)
        {
          Tile tile = tileLayer.getTileAt(p.x, p.y);
          if (tile != null)
          {
            brushList.add(new StatefulTile(p,i,tile));
          }
        }
      }
    }

    if (brushList.size() > 0)
    {
      TileGroup tileGroup = new TileGroup(map,brushList,null);
      map.addUserBrush(tileGroup.normalize());
      mapEditor.layerEditPanel.repaint();
    }
  }

}
