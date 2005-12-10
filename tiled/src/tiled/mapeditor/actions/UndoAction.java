/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class UndoAction extends AbstractAction
{
  private static final long serialVersionUID = -1129586889816507546L;

  private MapEditor mapEditor;
  
  public UndoAction(MapEditor mapEditor)
  {
    super("Undo");
    putValue(SHORT_DESCRIPTION, "Undo one action");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
    mapEditor.undoStack.undo();
    mapEditor.updateHistory();
    mapEditor.mapEditPanel.repaint();
  }
}