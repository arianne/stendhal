#!/usr/bin/env python3
#Tue Sep 10 23:32:58 CEST 2019, slightly tweaked -- omero

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
  * Replace your labeled image files from the tiled/world directory with
	the clean (unlabeled) ones from CVS.
  * Edit http://stendhalgame.org/wiki/Template:SmallWorldMap
	and add a link to your uploaded image.
"""

import sys
import glob
import operator
import os

from PIL import Image, ImageFont, ImageDraw

# get absolute path where script is located
dir_root = os.path.dirname(__file__)
if dir_root:
	os.chdir(dir_root)
dir_root = os.getcwd()
dir_world = os.path.join(dir_root, 'world')
script_name = os.path.basename(__file__)

protected = ('world', 'logo', 'reserved', 'empty', 'empty-white')

def showUsage():
	print ('Usage: {} [image-filenames...]'.format(script_name))
	print ('  This will label each PNG file passed as argument.')
	print ('  E.g: ./world/int_*.png will label all interiors, etc. etc.')
	print
	print ('Alternatively, you can run: {} -world'.format(script_name))
	print ('  This will label each PNG file in the world subdirectory.')

def do_label(fname):
	print ('Processing {}'.format(fname))
	# remove directory and file extension
	label = os.path.basename(fname).split('/')[-1].replace('.png', '')
	print ('Processing LABEL: {}'.format(label))

	img = Image.open(fname)
	img = img.convert('RGBA')


	# label
	draw = ImageDraw.Draw(img, 'RGBA')
	draw.fontmode = "0" # set to "1" to disable anti-aliasing (not documented in PIL module)
	font = ImageFont.truetype('../data/font/Carlito-Bold.ttf', 12)
	draw.text((6, 6), label, (  0,   0,   0, 255),font=font)
	draw.text((5, 5), label, (255, 255, 255, 255),font=font)

	# border
	img2 = Image.new('RGBA', img.size, (0, 0, 0, 0))
	draw = ImageDraw.Draw(img2, 'RGBA')
	draw.rectangle((0, 0, img.size[0] - 1, img.size[1] - 1), None, (255, 255, 192, 48))
	img.paste(img2, img2)

	img.save(fname)

if len(sys.argv) < 2:
	print('\nERROR: Not enough arguments\n')
	showUsage()
	sys.exit(1)
elif len(sys.argv) == 2 and sys.argv[1] == '-world':
	for fname in os.listdir(dir_world):
		if fname.endswith('.png'):
			absolute_fname = os.path.join(dir_world, fname)
			if fname.rstrip('.png') in protected:
				print('NOTE: skipping protected file: {}'.format(absolute_fname))
				continue
			do_label(absolute_fname)
else:
	# This doesn't work for me. --mort
	#for fname in reduce(operator.add, map(glob.glob, sys.argv[1:])):

	# This works for me. --omero
	for fname in list(sys.argv[1:]):
		if not fname.endswith('.png'):
			fname = '{}.png'.format(fname)
		absolute_fname = os.path.join(dir_world, fname)
		if fname.rstrip('.png') in protected:
			print('NOTE: skipping protected file: {}'.format(absolute_fname))
			continue
		# exit with error if file doesn't exist
		if not os.path.isfile(absolute_fname):
			print('\nERROR: Cannot process {}\n       File does not exist.'.format(absolute_fname))
			sys.exit(1)
		print ( 'Labeling {}...'.format(fname ))
		do_label(absolute_fname)

print ('All Done.')
