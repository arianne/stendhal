/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class SelectAllAction extends AbstractAction
{
  private static final long serialVersionUID = -1980981542520629392L;

  private MapEditor mapEditor;
  
  public SelectAllAction(MapEditor mapEditor)
  {
    super("All");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
    putValue(SHORT_DESCRIPTION, "Select entire map");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
  }
}