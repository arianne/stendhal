/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class CancelSelectionAction extends AbstractAction
{
  private static final long serialVersionUID = -6217788914300686640L;

  private MapEditor mapEditor;
  
  public CancelSelectionAction(MapEditor mapEditor)
  {
    super("None");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift A"));
    putValue(SHORT_DESCRIPTION, "Cancel selection");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (mapEditor.currentMap != null)
    {
      if (mapEditor.marqueeSelection != null)
      {
        mapEditor.currentMap.removeLayerSpecial(mapEditor.marqueeSelection);
      }

      mapEditor.marqueeSelection = null;
    }
  }
}