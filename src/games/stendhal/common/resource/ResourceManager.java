/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author silvio
 */
public class ResourceManager extends ResourceLocator
{
	private final static ResourceManager mSingleton      = new ResourceManager();
	private final static DefaultLocator  mDefaultLocator = new DefaultLocator();

	static
	{
		//mSingleton.setRootDirectory("../../");

		mSingleton.setDefaultLocator(mDefaultLocator);
		mSingleton.setLocator("file", mDefaultLocator);

		mSingleton.addScheme("sound" , "data/sounds");
		mSingleton.addScheme("music" , "data/music");
		mSingleton.addScheme("audio" , "data/sounds", "data/music");
		//mSingleton.addScheme("scheme", "data/xml");
	}
	
	private static class DefaultLocator implements ResourceLocator.Locator
	{
		public InputStream locate(URI uri)
		{
			try
			{
				return new FileInputStream(uri.getPath());
			}
			catch(FileNotFoundException ex) { }

			return null;
		}

		public boolean isLocatable(URI uri)
		{
			return new File(uri.getPath()).exists();
		}
	}

	private class SchemeLocator implements ResourceLocator.Locator
	{
		final ArrayList<File> mDirectories = new ArrayList<File>();

		void addSearchPath(String path)
		{
			File directory = new File(mRootDirectory, path);

			if(directory.isDirectory())
				mDirectories.add(directory);
		}

		File locateFile(URI uri)
		{
			for(File directory: mDirectories)
			{
				File file = new File(directory, uri.getPath());
				
				if(file.isFile())
					return file;
			}

			return null;
		}
		
		public InputStream locate(URI uri)
		{
			File file = locateFile(uri);

			try
			{
				if(file != null)
					return new FileInputStream(file);
			}
			catch(FileNotFoundException ex) { }

			return null;
		}

		public boolean isLocatable(URI uri)
		{
			return locateFile(uri) != null;
		}
	}

	private final HashMap<String,SchemeLocator> mSchemeLocators = new HashMap<String,SchemeLocator>();
	private File                                mRootDirectory  = null;

	public void addScheme(String name, String ...searchPaths)
	{
		assert name != null;
		assert !mSchemeLocators.containsKey(name): "scheme " + name + " was already added";

		if(!mSchemeLocators.containsKey(name))
		{
			SchemeLocator locator = new SchemeLocator();

			mSchemeLocators.put(name, locator);
			setLocator(name, locator);

			for(String path: searchPaths)
				locator.addSearchPath(path);
		}
	}

	public void addSearchPath(String scheme, String path)
	{
		SchemeLocator locator = mSchemeLocators.get(scheme);
		
		assert locator != null;
		assert path    != null;

		locator.addSearchPath(path);
	}

	public void setRootDirectory(String path)
	{
		if(path == null)
		{
			mRootDirectory = null;
		}
		else
		{
			File file = new File(path);
			
			if(file.isDirectory())
				mRootDirectory = file;
		}
	}

	public static ResourceManager get() { return mSingleton; }
}
