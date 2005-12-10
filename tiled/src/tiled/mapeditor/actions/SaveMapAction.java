/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * Saves the map
 * @author mtotz
 */
public class SaveMapAction extends AbstractAction
{
  private static final long serialVersionUID = -5617105173971065571L;

  private boolean withDialog;
  private MapEditor mapEditor;
  
  /** 
   * the action will popup a filechooser dialog when </i>withDialog</i> is true
   * (Save as...) 
   */
  public SaveMapAction(MapEditor mapEditor, boolean withDialog)
  {
    super("Save"+(withDialog ? " as..." : ""));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control "+(withDialog ? "shift " : "")+"S"));
    putValue(SHORT_DESCRIPTION, "Saves this map");
    this.mapEditor = mapEditor;
    this.withDialog = withDialog;
  }

  public void actionPerformed(ActionEvent e)
  {
    mapEditor.saveMap(withDialog);
  }

  
}
