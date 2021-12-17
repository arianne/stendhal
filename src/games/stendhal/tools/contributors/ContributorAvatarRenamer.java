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
