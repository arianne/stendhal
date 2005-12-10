/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class RedoAction extends AbstractAction
{
  private static final long serialVersionUID = 2467790103953607697L;
  
  private MapEditor mapEditor;

  public RedoAction(MapEditor mapEditor)
  {
    super("Redo");
    putValue(SHORT_DESCRIPTION, "Redo one action");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
    mapEditor.undoStack.redo();
    mapEditor.updateHistory();
    mapEditor.mapEditPanel.repaint();
  }
}