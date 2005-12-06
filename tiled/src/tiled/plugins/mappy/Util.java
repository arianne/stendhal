/*
 *  Mappy Plugin for Tiled, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 */

package tiled.plugins.mappy;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
	
	public static long readLongReverse(InputStream in) throws IOException {
		int a = in.read();
		int b = in.read();
		int c = in.read();
		int d = in.read();
	
		return (long)((a<<24)|(b<<16)|(c<<8)|d);
    }

	public static void writeLongReverse(long val, OutputStream out) throws IOException {
    	out.write((int)((val>>24)&0x000000FF));
    	out.write((int)((val>>16)&0x000000FF));
    	out.write((int)((val>>8)&0x000000FF));
    	out.write((int)((val)&0x000000FF));
    }
	
	public static int readShortReverse(InputStream in) throws IOException {
		int a = in.read();
		int b = in.read();
		return (int)((a<<8)|b);
    }

    public static int readShort(InputStream in) throws IOException {
    	int a = in.read();
        int b = in.read();
        return (int)(a|(b<<8));
    }
	
    public static void writeShort(int val, OutputStream out) throws IOException {
    	//TODO: this
    }
    
	public static Image readRawImage(InputStream in, int twidth, int theight) throws IOException {
	 	DirectColorModel cm = new DirectColorModel(16,0xF800,0x07E0,0x001F);
	
		int [] pixels = new int[twidth*theight+1];
		int i,j;
	
		for(i=0;i<theight;i++)
			for(j=0;j<twidth;j++) {
				pixels[i*twidth+j]=readShortReverse(in);
				//System.out.println(pixels[i*width+j]);
			}
	
		MemoryImageSource s = new MemoryImageSource(twidth,theight,cm,pixels,0,twidth);		
			
		return Toolkit.getDefaultToolkit().createImage(s);
		//return(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(orig.getSource(),new TransImageFilter(cm.getRGB(64305)))));
    }
}
