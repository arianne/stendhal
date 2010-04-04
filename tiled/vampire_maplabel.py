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
#!/usr/bin/env python

import sys
import glob
import operator
import os

from PIL import Image, ImageFont
from PIL.ImageDraw import ImageDraw

def do_label(fname):
    print "Processing %s" % fname
    # remove directory and file extension
    label = fname.split('/')[-1].replace(".png", "")

    img = Image.open(fname)
    img = img.convert("RGBA")

    # label
    draw = ImageDraw(img, "RGBA")
    font = ImageFont.truetype("arial.ttf", 12)
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
    print
    print "Alternatively, you can run: %s -world" % sys.argv[0]
    print "This will label each PNG file in the world subdirectory."
    sys.exit(0)
elif len(sys.argv) == 2 and sys.argv[1] == '-world':
    for fname in os.listdir('world'):
        if fname.endswith('.png') and fname != 'empty.png' and fname != 'world.png':
            do_label('world/' + fname)
else:
    # This doesn't work for me. --mort
    for fname in reduce(operator.add, map(glob.glob, sys.argv[1:])):
        print "Labeling %s..." % fname
        do_label(fname)


print "Done."

