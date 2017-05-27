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

/**
 * User login profile.
 */
public class Profile {
	/** Default server port. */
	static final int DEFAULT_SERVER_PORT = 32160;

	/** Old server names to remap. */
	private static final String[] OLD_SERVER_HOSTS = { "stendhal.ath.cx", "stendhal.game-host.org" };

	/** Default server name to replace old ones with. */
	private static final String NEW_SERVER_HOST = "stendhalgame.org";

	private String host;

	private int port;

	private String user;

	private String password;

	private String character;

	private String seed;

	public Profile() {
		this("", DEFAULT_SERVER_PORT, "", "");
	}

	private Profile(final String host, final int port, final String user, final String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;

	}

	//
	// Profile
	//

	/**
	 * Encode the login profile as a string.
	 *
	 * @return A string excoded form (with newlines).
	 */
	String encode() {
		StringBuilder sbuf;

		sbuf = new StringBuilder();
		sbuf.append(getHost());
		sbuf.append('\n');
		sbuf.append(getUser());
		sbuf.append('\n');
		sbuf.append(getPassword());
		sbuf.append('\n');
		sbuf.append(getPort());
		sbuf.append('\n');

		// TCP
		sbuf.append(true);

		return sbuf.toString();
	}

	public String getHost() {
		return host;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getCharacter() {
		return character;
	}

	public String getSeed() {
		return seed;
	}

	/**
	 * Decode a login profile from a string.
	 *
	 * @param info
	 *            The string encoded profile.
	 *
	 * @return A login profile.
	 */
	static Profile decode(final String info) {
		String[] params;
		Profile profile;
		String s;

		params = info.split("\n");

		profile = new Profile();

		/*
		 * Server Host
		 */
		if (params.length > 0) {
			s = params[0];

			for (final String host : OLD_SERVER_HOSTS) {
				if (s.equals(host)) {
					s = NEW_SERVER_HOST;
					break;
				}
			}

			if (s.length() != 0) {
				profile.setHost(s);
			}
		}

		/*
		 * User
		 */
		if (params.length > 1) {
			s = params[1];

			if (s.length() != 0) {
				profile.setUser(s);
			}
		}

		/*
		 * Password
		 */
		if (params.length > 2) {
			s = params[2];

			if (s.length() != 0) {
				profile.setPassword(s);
			}
		}

		/*
		 * Server Port
		 */
		if (params.length > 3) {
			s = params[3];

			if (s.length() != 0) {
				try {
					profile.setPort(Integer.parseInt(s));
				} catch (final NumberFormatException ex) {
					// use default port if port is not a number number
				}
			}
		}

		/*
		 * Server Protocol (TCP/UDP)
		 */
		// params[4]
		//
		// ignore this token for compatibility reasons
		// just add what every you want behind it
		return profile;
	}

	/**
	 * create a profile based on command line arguments
	 * <ul>
	 * <li>-u: username</li>
	 * <li>-p: password</li>
	 * <li>-c: character name (defaults to username)</li>
	 * <li>-h: hostname</li>
	 * <li>-P: port</li>
	 * <li>-S: pre authentication seed</li>
	 * </ul>
	 *
	 * @param args command line arguments
	 * @return profile
	 */
	public static Profile createFromCommandline(String[] args) {
		Profile profile = new Profile();
		int i = 0;
		while (i != args.length) {
			if (args[i].equals("-u")) {
				profile.setUser(args[i + 1]);
			} else if (args[i].equals("-p")) {
				profile.setPassword(args[i + 1]);
			} else if (args[i].equals("-c")) {
				profile.setCharacter(args[i + 1]);
			} else if (args[i].equals("-h")) {
				profile.setHost(args[i + 1]);
			} else if (args[i].equals("-P")) {
				profile.setPort(Integer.parseInt(args[i + 1]));
			} else if (args[i].equals("-S")) {
				profile.setSeed(args[i + 1]);
			}
			i++;
		}
		if (profile.getCharacter() == null) {
			profile.setCharacter(profile.getUser());
		}
		return profile;
	}

	/**
	 * checks whether all required attributes are defined
	 *
	 * @return true if all required attributes are defined, false if some are missing.
	 */
	public boolean isValid() {
		return (host != null) && (user != null) && (password != null)
			&& !host.equals("") && !user.equals("") && !password.equals("");
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public void setUser(final String user) {
		this.user = user;
	}


	public void setCharacter(String character) {
		this.character = character;
	}


	public void setSeed(String seed) {
		this.seed = seed;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Profile)) {
			return false;
		}

		final Profile profile = (Profile) obj;

		if (!getHost().equals(profile.getHost())) {
			return false;
		}

		if (getPort() != profile.getPort()) {
			return false;
		}

		if (!getUser().equals(profile.getUser())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return getHost().hashCode() ^ getUser().hashCode();
	}

	/**
	 * Get the label string. This label is used for the profile selection list.
	 *
	 * @return The label in the form of <em>user</em><strong>@</strong><em>server-host</em>[<strong>:</strong><em>port</em>].
	 */
	@Override
	public String toString() {
		StringBuilder sbuf;

		sbuf = new StringBuilder();
		sbuf.append(getUser());
		if (getCharacter() != null) {
			sbuf.append("/");
			sbuf.append(getCharacter());
		}
		sbuf.append('@');
		sbuf.append(getHost());

		if (getPort() != DEFAULT_SERVER_PORT) {
			sbuf.append(':');
			sbuf.append(getPort());
		}

		return sbuf.toString();
	}

}
