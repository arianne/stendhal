/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author silvio
 */
public class File implements Resource
{
	java.io.File mFile = null;
	String       mPath = null;

	public File(String pathName, boolean fromClassPath)
	{
		assert pathName != null;
		
		if(!fromClassPath)
			mFile = new java.io.File(pathName);

		mPath = pathName;
	}

	public InputStream getInputStream()
	{
		try
		{
			if(mFile != null)
				return new FileInputStream(mFile);
			else
				return this.getClass().getResourceAsStream("/" + mPath);
		}
		catch (FileNotFoundException exception)
		{
			assert false: exception;
		}

		return null;
	}

	public String getURI()
	{
		if(mFile != null)
			return "file://" + mFile.getAbsolutePath();

		return mPath;
	}

	public boolean exists()
	{
		if(mFile != null)
			return mFile.exists();

		InputStream stream = this.getClass().getResourceAsStream("/" + mPath);

		try
		{
			stream.close();
		}
		catch(IOException exception)
		{
			assert false: exception;
			stream = null;
		}

		return stream != null;
	}
}
