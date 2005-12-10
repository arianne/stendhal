/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class InverseSelectionAction extends AbstractAction
{
  private static final long serialVersionUID = -3030827051213056224L;

  private MapEditor mapEditor;
  
  public InverseSelectionAction(MapEditor mapEditor)
  {
    super("Invert");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control I"));
    putValue(SHORT_DESCRIPTION, "Inverse of the current selection");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (mapEditor.marqueeSelection != null)
    {
      mapEditor.marqueeSelection.invert();
      // mapView.repaint();
    }
  }
}