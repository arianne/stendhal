#!/usr/bin/env python

###########################################################################
#                      (C) Copyright 2023 - Stendhal                      #
###########################################################################
#                                                                         #
#   This program is free software; you can redistribute it and/or modify  #
#   it under the terms of the GNU General Public License as published by  #
#   the Free Software Foundation; either version 2 of the License, or     #
#   (at your option) any later version.                                   #
#                                                                         #
###########################################################################

import codecs, os

from PIL import Image, ImageDraw


os.chdir(os.path.dirname(__file__))
dir_target = os.getcwd()
os.chdir("../../../../")
dir_root = os.getcwd()
dir_config = os.path.normpath(os.path.join(dir_root, "data/conf/items"))
dir_sprites = os.path.normpath(os.path.join(dir_root, "data/sprites/items"))

tile_unused = os.path.normpath(os.path.join(dir_sprites, "meta/logic_unused.png"))

tilesets = {}

for xml in os.listdir(dir_config):
  xml = os.path.join(dir_config, xml)
  tmp = codecs.open(xml, "r", "utf-8")
  # normalize line endings
  content = tmp.read().replace("\r\n", "\n").replace("\r", "\n")
  tmp.close()

  lines = content.split("\n")
  itemName = None
  tileSet = None
  itemData = {}
  for line in lines:
    line = line.strip()
    if not itemName and line.startswith("<item name=\""):
      itemName = line.split("\"")[1]
      continue

    if not itemName:
      continue

    if line.startswith("<type "):
      spritePath = ""
      # ~ tileSet = ""
      tileId = -1
      for attr in line.rstrip("/>").split(" ")[1:]:
        if "=" not in attr:
          continue
        attr = attr.replace("\"", "").split("=")
        key = attr[0]
        value = attr[1]

        if key == "class":
          spritePath = value
        elif spritePath and key == "subclass":
          spritePath = os.path.join(spritePath, value) + ".png"
        elif key == "tileid":
          if ":" not in value:
            continue
          vtmp = value.split(":")
          tileSet = vtmp[0]
          try:
            if not tileSet:
              raise ValueError
            tileId = int(vtmp[1])
          except ValueError:
            print("WARNING: invalid tile ID: \"{}\"".format(value))

      if spritePath.endswith(".png") and tileId > -1:
        itemData["image"] = spritePath
        # ~ itemData["tileset"] = tileSet
        itemData["tileid"] = tileId

    if line == "</item>":
      # add to item list
      if itemName and tileSet and itemData:
        if tileSet not in tilesets:
          tilesets[tileSet] = {}
        tilesets[tileSet][itemName] = itemData
      itemName = None
      tileSet = None
      itemData = {}
      continue


img_bg = Image.new("RGB", (32, 32))
# draw white background image with black border
ImageDraw.Draw(img_bg).rectangle(((0, 0), (31, 31)), (255, 255, 255), (0, 0, 0))
tilesPerRow = 13

for tileSet in tilesets:
  print("\nCreating item tileset \"{}\" for items:".format(tileSet))
  items = tilesets[tileSet]
  tiles = {}
  for itemName in tuple(sorted(items.keys())):
    print("  " + itemName)
    tiles[items[itemName]["tileid"]] = itemName

  tileRows = []
  currentRow = []
  for tileId in range(0, sorted(tiles.keys())[-1] + 1):
    if tileId > 0 and not tileId % tilesPerRow:
      tileRows.append(tuple(currentRow))
      currentRow = []
    currentRow.append(tileId)
  if currentRow:
    tileRows.append(tuple(currentRow))

  if not tileRows:
    continue

  # create base image with pink background
  tileSetImage = Image.new("RGB", (len(tileRows[0]) * 32, len(tileRows) * 32), (255, 0, 255))
  out = os.path.join(dir_target, tileSet)
  tileIdx = -1
  for row in range(len(tileRows)):
    offsetY = row * 32
    rgroup = tileRows[row]
    for col in range(len(rgroup)):
      tileIdx += 1
      offsetX = col * 32
      tileId = tileRows[row][col]
      tileImage = img_bg.copy()
      if tileId in tiles:
        spritePath = os.path.normpath(os.path.join(dir_sprites, items[tiles[tileIdx]]["image"]))
        sprite = Image.open(spritePath).convert("RGBA")
        tileImage.paste(sprite, None, sprite)
        sprite.close()
      else:
        if os.path.isfile(tile_unused):
          tileImage = Image.open(tile_unused)
        else:
          tileImage = Image.new("RGB", (32, 32), (255, 0, 255))

      tileSetImage.paste(tileImage, (offsetX, offsetY))
      tileImage.close()

  # convert to indexed color & save
  tileSetImage.convert("P").save(out)
  tileSetImage.close()

img_bg.close()
