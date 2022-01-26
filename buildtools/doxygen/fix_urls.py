#!/usr/bin/env python

# Doxygen is trimming the .html suffix from external references in tag files.
# So this script is a hack to fix URLs after HTML documentation is generated
# until a fix is determined for tag files.


import os, codecs

dir_root = os.path.normpath(os.path.join(os.path.dirname(__file__), "../../"))
os.chdir(dir_root)

delim = "docs.oracle.com/en/java/javase/16/docs/api/"

for ROOT, DIRS, FILES in os.walk("build/doxygen/html/"):
	for file in FILES:
		file = os.path.normpath(os.path.join(ROOT, file))

		if os.path.isfile(file) and file.endswith(".html") or file.endswith(".js"):
			lines = ()
			new_lines = []
			buffer = codecs.open(file, "r", "utf-8")
			if buffer:
				lines = tuple(buffer.readlines())
				buffer.close()

			for li in lines:
				if delim in li:
					tmp = li.split(delim)
					url = tmp[1].split("\"")[0]
					if not url.lower().endswith(".html"):
						li = "{}{}{}.html{}".format(tmp[0], delim, url, tmp[1].lstrip(url))

				new_lines.append(li)

			if tuple(new_lines) != lines:
				print("\nUpdating file:\n  {}".format(file))

				buffer = codecs.open(file, "w", "utf-8")
				if buffer:
					buffer.write("\n".join(new_lines))
					buffer.close()
