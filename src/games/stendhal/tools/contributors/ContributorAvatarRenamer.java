/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools.contributors;

import java.io.File;

public class ContributorAvatarRenamer {

	public static void main(String[] args) {
		File folder = new File("/tmp/z");
		int index = 0;
		for (File file : folder.listFiles()) {
			file.renameTo(new File("/tmp/z/" + index + ".png"));
			index++;
		}
	}
}

/*
<!DOCTYPE html>
<html>
<head>
<style>
img {
	width: 64px;
	height: 64px;
	border-radius: 50%;
	background-color: #FFF;
}
div {
	width: 1000px;
	background-color: #FFA;
	padding: 1em;
}
</style>
</head>
<body>
<div>
*/
