#!/usr/bin/env python3

"""
This script is used to create the world map with labeled zone names.
It adds labels and borders around the small zone images.

To create the labeled world map:
  * Update from CVS
  * Open a command line
  * Change to your stendhal directory
  * Run
      ant rendermaps
  * Change to the tiled directory
  * Run
      python vampire_maplabel.py -world
  * Open tiled/world/world.tmx in tiled
  * Set the floor layer to opaque (no transparency)
  * Set the zoom to 1:1 (CTRL-0)
  * From the file menu, select "Save as Image"
  * Crop the black border (e.g. use "Crop Automatically" in The GIMP)
  * Upload the image, e.g. at http://imageshack.us
  * Replace your labeled image files from the tiled/world directory with the clean (unlabeled) ones
    from CVS.
  * Edit http://stendhalgame.org/wiki/Template:SmallWorldMap and add a link to your uploaded image.
"""

import sys
import glob
import operator
import os

from PIL import Image, ImageFont, ImageDraw


script_name = os.path.basename(__file__)

# directory from where script was executed
dir_start = os.getcwd()
# path to root directory
os.chdir(os.path.join(os.path.dirname(__file__), '../../'))
dir_root = os.getcwd()
# directory relative to root where images are located
subdir_world = os.path.normpath('data/maps/world')
# absolute path to directory where images are located
dir_world = os.path.join(dir_root, subdir_world)

protected = ('world', 'logo', 'reserved', 'empty', 'empty-white')

def showUsage():
	print('Usage: {} [image-filenames...]'.format(script_name))
	print('  This will label each PNG file passed as argument.')
	print('  E.g: data/maps/world/int_*.png will label all\n    interiors, etc.')
	print('  Note: If filename parameters aren\'t found they\n    are assumed to be nested within the\n    {} directory.'.format(subdir_world))
	print()
	print('Alternatively, you can run: {} -world'.format(script_name))
	print('  This will label each PNG file in the world subdirectory.')

processed = 0
def do_label(fname):
	global processed

	print('Processing {}'.format(fname))
	# remove directory and file extension
	label = os.path.basename(fname).split('/')[-1].replace('.png', '')
	print('Processing LABEL: {}'.format(label))

	img = Image.open(fname)
	img = img.convert('RGBA')


	# label
	draw = ImageDraw.Draw(img, 'RGBA')
	draw.fontmode = "0" # set to "1" to disable anti-aliasing (not documented in PIL module)
	font = ImageFont.truetype('../font/Carlito-Bold.ttf', 12)
	draw.text((6, 6), label, (  0,   0,   0, 255),font=font)
	draw.text((5, 5), label, (255, 255, 255, 255),font=font)

	# border
	img2 = Image.new('RGBA', img.size, (0, 0, 0, 0))
	draw = ImageDraw.Draw(img2, 'RGBA')
	draw.rectangle((0, 0, img.size[0] - 1, img.size[1] - 1), None, (255, 255, 192, 48))
	img.paste(img2, img2)

	img.save(fname)
	processed += 1


## Checks file path & converts to absolute.
#
#  @param imagepath
#    Path to image file.
def checkAbsPath(imagepath):
	if not imagepath.lower().endswith('.png'):
			# only PNG images supported
			imagepath += '.png'
	imagepath = os.path.normpath(imagepath)

	# check from root directory
	if os.path.isfile(imagepath):
		return os.path.abspath(imagepath)

	# check from directory where script was executed
	os.chdir(dir_start)
	if os.path.isfile(imagepath):
		imagepath = os.path.abspath(imagepath)
	else:
		# assume file is nested in `subdir_world`
		imagepath = os.path.join(dir_world, imagepath)
	# change back to root dir remaining execution
	os.chdir(dir_root)

	return imagepath


if len(sys.argv) < 2:
	print('\nERROR: Not enough arguments\n')
	showUsage()
	sys.exit(1)


if len(sys.argv) == 2 and sys.argv[1] == '-world':
	for fname in os.listdir(dir_world):
		if fname.endswith('.png'):
			absolute_fname = os.path.join(dir_world, fname)
			if fname.rstrip('.png') in protected:
				print('NOTE: skipping protected file: {}'.format(absolute_fname))
				continue
			do_label(absolute_fname)
else:
	for fname in list(sys.argv[1:]):
		absolute_fname = checkAbsPath(fname)
		if fname.rstrip('.png') in protected:
			print('NOTE: skipping protected file: {}'.format(absolute_fname))
			continue
		# exit with error if file doesn't exist
		if not os.path.isfile(absolute_fname):
			print('\nERROR: Cannot process {}\n       File does not exist.'.format(absolute_fname))
			sys.exit(1)
		print('Labeling {}...'.format(fname ))
		do_label(absolute_fname)

if processed == 0:
	print('\nWARNING: no images were processed')
else:
	print('\nprocessed {} {}'.format(processed, 'image' if processed == 1 else 'images'))
