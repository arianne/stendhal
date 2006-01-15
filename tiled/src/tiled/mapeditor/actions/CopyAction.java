/**
 * 
 */
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

public class CopyAction extends AbstractAction
{
  private static final long serialVersionUID = -7093838522430390018L;

  private MapEditor mapEditor;
  
  public CopyAction(MapEditor mapEditor)
  {
    super("Copy");
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
    putValue(SHORT_DESCRIPTION, "Copy");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent evt)
  {
  }
}