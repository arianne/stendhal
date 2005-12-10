/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * Saves the current map as an image
 * @author mtotz
 */
public class SaveAsImageAction extends AbstractAction
{
  private static final long serialVersionUID = -1390871258039401349L;

  private MapEditor mapEditor;
  
  public SaveAsImageAction(MapEditor mapEditor)
  {
    super("Save as Image...");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift I"));
    putValue(SHORT_DESCRIPTION, "Save current map as an image");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    mapEditor.saveMapImage();
  }

}
