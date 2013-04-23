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
package games.stendhal.client.gui.login;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * User login profile list.
 */
class ProfileList implements Iterable<Profile >{
	protected ArrayList<Profile> profiles;

	/**
	 * Create an empty profile list.
	 */
	public ProfileList() {
		profiles = new ArrayList<Profile>();
	}

	//
	// ProfileList
	//

	/**
	 * Add a profile. This will remove duplicates.
	 *
	 * @param profile
	 *            A user login profile.
	 */
	void add(final Profile profile) {
		/*
		 * Keep one equivalent entry (can't use HasSet and preserve order)
		 */
		profiles.remove(profile);
		profiles.add(profile);
	}

	/**
	 * Remove all profiles.
	 */
	private void clear() {
		profiles.clear();
	}

	/**
	 * Get an iterator of profiles.
	 *
	 * @return An iterator of profiles.
	 */
	@Override
	public Iterator<Profile> iterator() {
		return profiles.iterator();
	}

	/**
	 * Load a list of profiles from an input stream. This will replace any
	 * existing list.
	 * @param in The Stream to read
	 * @throws IOException if any IO operation fails
	 */
	void load(final InputStream in) throws IOException {
		final Encoder codec = new Encoder();
		String s;

		final BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));

		clear();

		while ((s = r.readLine()) != null) {
			add(Profile.decode(codec.decode(s)));
		}
	}

	/**
	 * Remove a profile.
	 *
	 * @param profile
	 *            A user login profile.
	 */
	void remove(final Profile profile) {
		profiles.remove(profile);
	}

	/**
	 * Save a list of profiles to an output stream.
	 *
	 * @param out
	 *            The stream to write.
	 * @throws IOException if any IO operation fails
	 */
	void save(final OutputStream out) throws IOException {
		final Encoder codec = new Encoder();

		final Writer writer = new OutputStreamWriter(out, "UTF-8");

		try {
			for (Profile p : this) {
				writer.write(codec.encode(p.encode()));
				writer.write(System.getProperty("line.separator"));
			}
		} finally {
			writer.flush();
		}
	}

	//
	//

	public static void main(final String[] args) throws Exception {
		ProfileList list;
		InputStream in;

		if (args.length != 1) {
			System.err.println("Usage: java " + ProfileList.class.getName()
					+ " <user.dat>");

			System.exit(1);
		}

		list = new ProfileList();

		in = new java.io.FileInputStream(args[0]);

		try {
			list.load(in);

		} finally {
			in.close();
		}

		for (Profile p : list) {
			System.out.println(p);
		}
	}
}
