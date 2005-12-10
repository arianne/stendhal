/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * Creates a new map from scratch
 * 
 * @author mtotz
 */
public class NewMapAction extends AbstractAction
{
  private static final long serialVersionUID = 1879486472908143291L;

  private MapEditor mapEditor;
  
  public NewMapAction(MapEditor mapEditor)
  {
    super("New...");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
    putValue(SHORT_DESCRIPTION, "Start a new map");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    mapEditor.newMap();
  }

}
