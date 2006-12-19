#!/usr/bin/env python

import sys
import glob
import operator

from PIL import Image, ImageFont
from PIL.ImageDraw import ImageDraw

def do_label(fname):
	label = fname.replace(".png", "")

	img = Image.open(fname)
	img = img.convert("RGBA")

	# label
	draw = ImageDraw(img, "RGBA")
	font = ImageFont.truetype("/usr/share/fonts/truetype/freefont/FreeSans.ttf", 12)
	draw.setfont(font)
	draw.text((6, 6), label, (0, 0, 0, 255))
	draw.text((5, 5), label, (255, 255, 255, 255))

	# border
	img2 = Image.new("RGBA", img.size, (0, 0, 0, 0))
	draw = ImageDraw(img2, "RGBA")
	draw.rectangle((0, 0, img.size[0] - 1, img.size[1] - 1), None, (255, 255, 192, 48))
	img.paste(img2, img2)

	img.save(fname)

if len(sys.argv) < 2:
	print "Usage: %s [image-filenames...]" % sys.argv[0]
	sys.exit(0)

for fname in reduce(operator.add, map(glob.glob, sys.argv[1:])):
	print "Labeling %s..." % fname
	do_label(fname)

print "Done."

