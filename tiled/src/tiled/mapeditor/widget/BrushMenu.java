/**
 * 
 */
package tiled.mapeditor.widget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import tiled.core.Map;
import tiled.core.TileGroup;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.brush.MultiTileBrush;
import tiled.mapeditor.brush.ShapeBrush;
import tiled.mapeditor.brush.TileGroupBrush;
import tiled.mapeditor.util.MapChangeListener;
import tiled.mapeditor.util.MapChangedEvent;

/**
 * Brush Menu
 * @author mtotz
 */
public class BrushMenu extends JComboBox implements MapChangeListener
{
  private static final long serialVersionUID = 1L;
  /** the map */
  private Map map;
  /** the mapeditor instance */
  private MapEditor mapEditor;
  /** */
  private List<BrushWrapper> defaultBrushes;


  public BrushMenu(MapEditor mapEditor)
  {
    this.mapEditor = mapEditor;
    
    defaultBrushes = new ArrayList<BrushWrapper>();
    defaultBrushes.add(new BrushWrapper(new MultiTileBrush()));
    defaultBrushes.add(new BrushWrapper(ShapeBrush.makeRectBrush(1,1)));
    defaultBrushes.add(new BrushWrapper(ShapeBrush.makeRectBrush(2,2)));
    defaultBrushes.add(new BrushWrapper(ShapeBrush.makeRectBrush(4,4)));
    defaultBrushes.add(new BrushWrapper(ShapeBrush.makeRectBrush(8,8)));
    
    addActionListener(new ActionListener()
    {
    
      public void actionPerformed(ActionEvent e)
      {
        Object o = getSelectedItem();
        if (o instanceof BrushWrapper)
        {
          BrushMenu.this.mapEditor.setBrush(((BrushWrapper)o).brush);
        }
      }
    });
  }
  
  /** sets the map */
  public void setMap(Map map)
  {
    this.map = map;
    if (map != null)
    {
      map.addMapChangeListener(this);
      updateBrushes();
    }
  }
  
  /** refreshes the brush list once the map changes */
  public void mapChanged(MapChangedEvent e)
  {
    if (e.getType() != MapChangedEvent.Type.BRUSHES)
      return;
    
    updateBrushes();
  }

  /** updates the brush list */
  private void updateBrushes()
  {
    List<TileGroup> groupList = map.getUserBrushes();
    
    List<BrushWrapper> brushes = new ArrayList<BrushWrapper>();
    brushes.addAll(defaultBrushes);
    
    for (TileGroup group : groupList)
    {
      brushes.add(new BrushWrapper(new TileGroupBrush(group)));
    }
    
    setModel(new DefaultComboBoxModel(brushes.toArray(new BrushWrapper[brushes.size()])));
  }
  

  /** wraps a brush to create a nice toString() (for the selection box) */
  private class BrushWrapper
  {
    public Brush brush;
    
    public BrushWrapper(Brush brush)
    {
      this.brush = brush;
    }

    /**  */
    public String toString()
    {
      return brush.getName();
    }
    
  }
}
