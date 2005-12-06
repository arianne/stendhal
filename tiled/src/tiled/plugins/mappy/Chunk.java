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

import java.io.*;

public class Chunk {

    private String headerTag;
    private int chunkSize;
    private ByteArrayInputStream bais;
    private ByteArrayOutputStream out;
    
    public Chunk(InputStream in) throws IOException {
		byte[] header = new byte[4];
		byte[] data;
		int readSize;	
		
	    in.read(header);
		headerTag = new String(header);
		chunkSize = (int)Util.readLongReverse(in);
		if(chunkSize > 0) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			data = new byte[chunkSize];
			readSize = in.read(data, 0, chunkSize);
			if(readSize!=chunkSize)
				throw new IOException("Incomplete read!");
			baos.write(data);
			bais = new ByteArrayInputStream(baos.toByteArray());
		}
    }

    public Chunk(String header) {
    	headerTag = header;
    	out = new ByteArrayOutputStream();
    }
    
    public boolean isGood() {
    	return chunkSize > 0;
    }

    public boolean equals(Object o) {
		if(o instanceof String) {
			return o.equals(headerTag);
		}else if(o instanceof Chunk) {
			return ((Chunk)o).headerTag.equals(headerTag);
		}
		return false;
    }

    public int size() {
    	return headerTag.length()+out.size()+4;
    }
    
    public void write(OutputStream out) throws IOException {
    	byte [] data = this.out.toByteArray();
    	out.write(headerTag.getBytes());
    	Util.writeLongReverse((long)(data.length-(headerTag.length()+4)), out);
    	out.write(data);
    }  
    
    public InputStream getInputStream() {
    	return bais;
    }
    
    public OutputStream getOutputStream() {
    	return out;
    }
}
