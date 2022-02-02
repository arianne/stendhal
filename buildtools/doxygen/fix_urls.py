#!/usr/bin/env python

# Doxygen 1.9.3 has some issues with appending extensions to some tags files
# URLs, & stripping them from others. This script scans the build directory
# & updates documentation with proper URL paths.


import os, sys, codecs

dir_root = os.path.normpath(os.path.join(os.path.dirname(__file__), "../../"))
os.chdir(dir_root)

suffix = ".htm"
delim = "https://developer.android.com/reference/"

print("Fixing external links for URLs: {}".format(delim))

updated_count = 0
for ROOT, DIRS, FILES in os.walk("build/doxygen/html/"):
	for file in FILES:
		file = os.path.normpath(os.path.join(ROOT, file))

		if os.path.isfile(file) and file.endswith(suffix) or file.endswith(".js"):
			lines = ()
			new_lines = []
			buffer = codecs.open(file, "r", "utf-8")
			if buffer:
				plaintext = buffer.read().replace("\r\n", "\n").replace("\r", "\n")
				buffer.close()

				if not delim in plaintext:
					continue

				lines = tuple(plaintext.split("\n"))

			for li in lines:
				if delim in li:
					tmp = li.split(delim)
					if len(tmp) > 1 and suffix in tmp[1]:
						before = tmp[0]
						tmp = tmp[1].split(suffix)
						after = ""
						if len(tmp) > 1:
							after = tmp[1]
						url = "{}{}".format(delim, tmp[0])

						li = "{}{}{}".format(before, url, after)

				new_lines.append(li)

			if tuple(new_lines) != lines:
				sys.stdout.write("\nUpdating file:\n  {} ...".format(file))

				buffer = codecs.open(file, "w", "utf-8")
				if buffer:
					buffer.write("\n".join(new_lines))
					buffer.close()

				print(" done")
				updated_count = updated_count + 1

print("\nJob done (updated {} files)".format(updated_count))
