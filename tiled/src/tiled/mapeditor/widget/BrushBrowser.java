/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.widget;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tiled.core.Map;
import tiled.mapeditor.MapEditor;

/**
 * A panel that allows selecting a brush from a set of presets.
 */
public class BrushBrowser extends JPanel
{
  private static final long serialVersionUID = -8809620337284162963L;


  /** the brush list panel */
  private BrushList brushList;

  public BrushBrowser(MapEditor mapEditor)
  {
    this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    
    add(new JButton(mapEditor.createSingleLayerBrushAction));
    add(new JButton(mapEditor.createMultiLayerBrushAction));
    
    JScrollPane brushListScrollPane = new JScrollPane();
    brushList = new BrushList(mapEditor);
    brushListScrollPane.getViewport().setView(brushList);
    
    add(brushListScrollPane);
  }
  
  /** sets the map */
  public void setMap(Map map)
  {
    brushList.setMap(map);
  }
  
}
