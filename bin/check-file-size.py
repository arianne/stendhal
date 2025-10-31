#!/usr/bin/env python3

'''
***************************************************************************
*                    Copyright © 2025 - Faiumoni e. V.                    *
***************************************************************************
***************************************************************************
*                                                                         *
*   This program is free software; you can redistribute it and/or modify  *
*   it under the terms of the GNU General Public License as published by  *
*   the Free Software Foundation; either version 2 of the License, or     *
*   (at your option) any later version.                                   *
*                                                                         *
***************************************************************************
'''

import errno
import os
import sys

from pathlib import Path


dir_root = Path(__file__).parent
if "bin" == dir_root.name:
	dir_root = Path(dir_root.parent)
dir_music = Path(dir_root, "data/music")
dir_sounds = Path(dir_root, "data/sounds")


## Checks if a file is larger than 2MB.
#
#  On systems with low resources, the Java client struggles to load large audio files.
#
#  @param path
#    File path.
#  @return
#    `EFBIG` if file is larger than 2MB, 0 otherwise.
def checkAudioFileSize(path):
	if not path.is_file():
		# ignore if audio file doesn't exist
		print("WARNING: Ignoring file not found: {}".format(path))
		return 0

	if path.stat().st_size > 1048576 * 2:
		# warn if file size > 2MB
		return errno.EFBIG
	return 0

## Checks directory for large audio files.
#
#  @param path
#    Directory path to be walked.
#  @return
#    `EFBIG` if any file larger than 2MB found, 0 otherwise.
def checkAudioDir(path):
	if not path.is_dir():
		# ignore if audio directory doesn't exist
		print("WARNING: Ignoring directory not found: {}".format(path))
		return 0

	res = 0
	for ROOT, DIRS, FILES in path.walk():
		for filename in FILES:
			filepath = Path(ROOT, filename)
			if filepath.suffix.lower().lstrip(".") not in ("ogg", "oga", "mid", "midi"):
				# only check supported file types
				continue

			code = checkAudioFileSize(filepath)
			if code == errno.EFBIG:
				res = code
				sys.stderr.write("WARNING: Audio file size larger than 2MB: {}\n".format(str(filepath)[len(str(dir_root)):].lstrip("/\\")))

	return res

def main():
	res = 0
	for audio_dir in dir_music, dir_sounds:
		code = checkAudioDir(audio_dir)
		if code != 0:
			res = code
	if res == 0:
		print("No large audio files detected")
	return res

if __name__ == "__main__":
	# work from source tree root
	os.chdir(dir_root)
	sys.exit(main())
