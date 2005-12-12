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
  }
}