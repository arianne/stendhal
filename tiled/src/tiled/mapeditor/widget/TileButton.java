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

import java.awt.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import tiled.core.*;
import tiled.mapeditor.util.*;


public class TileButton extends JButton
{
  private static final long serialVersionUID = 5253712806738421063L;
    private Tile tile;
    private boolean maintainAspect;
    private EventListenerList tileSelectionListeners;

    public TileButton( Tile t, Dimension d ) {
        setMargin(new Insets(0, 0, 0, 0));
        maintainAspect = false;
        setTile( t );
    }

    public TileButton( Dimension d ) {
        this( null, d );
    }

    public TileButton( Tile t ) {
        this( t, null );
    }

    public TileButton( ) {
        this( null, null );
    }

    public void setTile( Tile t ) {
        tile = t;
        ImageIcon icon = null;
        Insets i = getInsets();

        if (tile != null && tile.getImage() != null) {
            Image tileImg = tile.getImage();
            int imgWidth = tileImg.getWidth(null);
            int w = getWidth() - i.left - i.right;

            if (imgWidth > w) {
                icon = new ImageIcon(tileImg.getScaledInstance(w,
                            ((tileImg.getHeight(null) * w) / imgWidth),
                            Image.SCALE_SMOOTH));
            } else {
                icon = new ImageIcon(tileImg);
            }
        }

        setIcon(icon);
    }

    /*
     *  Methods for Size Information 
     */
    /*
    private Dimension calculatePreferredSize( ) {
        Insets i = getInsets( );

        if( tile != null ) {
            int w = tile.getWidth( ) + i.left + i.right;
            int h = tile.getHeight( ) + i.top + i.bottom;
            return new Dimension( w, h );
        }

        return null;
    }

    private Dimension calculateInnerSize( ) {
        Insets i = getInsets( );
        int w = getWidth( ) - i.left - i.right;
        int h = getHeight( ) - i.top - i.bottom; 
        return new Dimension( w, h );
    }

    public Dimension getPreferredSize( ) {
        Dimension d;

        if( maintainAspect ) {
            if( ( d = calculatePreferredSize( ) ) != null )	{
                Dimension s = new Dimension( );
                s.width  = getWidth( );
                s.height = (int)
                    (getWidth( ) / ( (double)d.width / (double)d.height) );
                return s;
            }
        }
        else if( size != null ) {
            return size;
        }
        else if( ( d = calculatePreferredSize( ) ) != null ) {
            return d;
        }
        d = super.getPreferredSize( );
        if( d.height < 2 ) {
            d.height = 5;
        }
        if( d.width < 2 ) {
            d.height = 5;
        }
        return d;
    }
    */

    public void setMaintainAspect( boolean v ) {
        maintainAspect = v;
    }

    public boolean isAspectMaintained( ) {
        return maintainAspect;
    }

    /**
     * Adds a tile selection listener. The listener will be notified when the
     * tile shown by the tile button changes.
     */
    public void addTileSelectionListener( TileSelectionListener l ) {
        tileSelectionListeners.add( TileSelectionListener.class, l );
    }

    /**
     * Removes a tile selection listener.
     */
    public void removeTileSelectionListener( TileSelectionListener l ) {
        tileSelectionListeners.remove( TileSelectionListener.class, l );
    }

    /**
     * Notifies all registered tile selection listeners about a newly selected
     * tile.
     */
    protected void fireActionPerformed( TileSelectionEvent e ) {
        Object[] listeners = tileSelectionListeners.getListenerList( );

        for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if( listeners[i] == TileSelectionListener.class ) {
                ((TileSelectionListener)listeners[i + 1]).tileSelected( e );
            }
        }
    }
}
