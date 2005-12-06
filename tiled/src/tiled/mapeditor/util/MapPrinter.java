/*
 * Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 */
package tiled.mapeditor.util;

import java.awt.*;
import java.awt.print.*;

import javax.swing.JPanel;
import javax.swing.RepaintManager;

public final class MapPrinter implements Printable {

	private PrinterJob printJob;
	private JPanel printed;
	
	public void print(JPanel p) throws PrinterException {
		RepaintManager currentManager = 
					  RepaintManager.currentManager(p);
		printed = p;
		currentManager.setDoubleBufferingEnabled(false);
		printJob = PrinterJob.getPrinterJob();		
		if(printJob.printDialog()) {			
			printJob.setPrintable(this,printJob.defaultPage());
			printJob.print();
		}
		currentManager.setDoubleBufferingEnabled(true);
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
		throws PrinterException {
					
			Graphics2D graphics2D = (Graphics2D) graphics;
			graphics2D.translate(pageFormat.getImageableX(),pageFormat.getImageableY());
			//graphics2D.scale(graphics2D.getClipBounds().getWidth()/printed.getWidth(),graphics2D.getClipBounds().getHeight()/printed.getHeight());			
			graphics2D.drawLine(72,72,160,160);
			printed.paint(graphics2D);
			
		return Printable.PAGE_EXISTS;
	}

}
