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

import codecs, math, os

from PIL import Image, ImageDraw, ImageOps


os.chdir(os.path.dirname(__file__))
# logic tilesets root
dir_logic = os.getcwd()
os.chdir("../../../")
# source tree root
dir_root = os.getcwd()

# paths to parse configurations
dir_config_item = os.path.normpath(os.path.join(dir_root, "data/conf/items"))
dir_config_creature = os.path.normpath(os.path.join(dir_root, "data/conf/creatures"))
# paths to sprite images
dir_sprite_item = os.path.normpath(os.path.join(dir_root, "data/sprites/items"))
dir_sprite_creature = os.path.normpath(os.path.join(dir_root, "data/sprites/monsters"))
# paths to output tile images
dir_target_item = os.path.join(dir_logic, "item")
dir_target_creature = os.path.join(dir_logic, "creature")

_file_unused = os.path.normpath(os.path.join(dir_sprite_item, "meta/logic_unused.png"))
tile_unused = Image.open(_file_unused) if os.path.isfile(_file_unused) else Image.new("RGB", (32, 32), (255, 0, 255))

tile_bg = Image.new("RGB", (32, 32))
# draw white background image with black border
ImageDraw.Draw(tile_bg).rectangle(((0, 0), (31, 31)), (255, 255, 255), (0, 0, 0))
tilesPerRow = 8


## Reads config text.
#
#  @param filepath
#      Path to file to be read.
#  @return
#      Lines from text.
def readConfig(filepath):
  tmp = codecs.open(filepath, "r", "utf-8")
  # normalize line endings
  content = tmp.read().replace("\r\n", "\n").replace("\r", "\n")
  tmp.close()
  # remove empty lines
  while "\n\n" in content:
    content = content.replace("\n\n", "\n")
  return content.split("\n")


# special instructions for creature tiles
creature_conf = {}
file_creature_conf = os.path.join(dir_logic, "creatures.conf")
if os.path.isfile(file_creature_conf):
  for line in readConfig(file_creature_conf):
    line = line.strip()
    if not line or line.startswith("#") or "=" not in line:
      continue
    idx = line.index("=")
    key = line[0:idx].strip()
    value = line[idx+1:].strip()
    if not key:
      print("WARNING: empty key in creatures.conf")
      continue
    if not value:
      print("WARNING: empty value for \"{}\" in creatures.conf".format(key))
      continue

    if key not in creature_conf:
      creature_conf[key] = {}
    conf = creature_conf[key]
    if "flags" not in conf:
      conf["flags"] = []

    for v in value.split(","):
      v = v.strip()
      if "=" in v:
        tmp = v.split("=")
        conf[tmp[0].strip()] = tmp[1].strip()
      else:
        conf["flags"].append(v)

    creature_conf[key] = conf


## Builds an image from tiles data.
#
#  @param tiles
#      Tiles defintions.
#  @param spriteDir
#      Root path of sprite images tree.
#  @param defs
#      Entities or items definitions.
#  @param itemType
#      `True` for items, `False` for creatures.
#  @return
#      Generated tileset image.
def buildTileSet(tiles, spriteDir, defs, itemType=True):
  global tile_unused, tile_bg, tilesPerRow, creature_conf

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
    return None

  # create base image with pink background
  tileSetImage = Image.new("RGB", (len(tileRows[0]) * 32, len(tileRows) * 32), (255, 0, 255))
  tileIdx = -1
  for row in range(len(tileRows)):
    offsetY = row * 32
    rgroup = tileRows[row]
    for col in range(len(rgroup)):
      tileIdx += 1
      offsetX = col * 32
      tileId = tileRows[row][col]
      tileImage = tile_bg.copy()
      if tileId in tiles:
        name = tiles[tileIdx]
        spritePath = os.path.normpath(os.path.join(spriteDir, defs[name]["image"]))
        sprite = Image.open(spritePath).convert("RGBA")
        if not itemType:
          conf = {"flags": []} if name not in creature_conf else creature_conf[name]
          noscale = "noscale" in conf["flags"]
          nopad = "nopad" in conf["flags"]
          alignX = "left" if "alignX" not in conf else conf["alignX"]
          alignY = "center" if "alignY" not in conf else conf["alignY"]
          # TODO: add text
          text = None if "text" not in conf else conf["text"]

          if "downscale" in conf:
            dfactor = 1
            try:
              dfactor = int(conf["downscale"])
            except ValueError:
              print("ERROR: downscale factor for \"{}\" must be an integer")
            newWidth = math.floor(sprite.width / dfactor)
            newHeight = math.floor(sprite.height / dfactor)
            sprite = sprite.resize((newWidth, newHeight), Image.BICUBIC)

          sliceW = math.floor(sprite.width / 3)
          sliceH = math.floor(sprite.height / 4)
          sliceX = sliceW
          sliceY = sliceH * (2 if "sliceY" not in conf else int(conf["sliceY"]))
          sprite = sprite.crop((sliceX, sliceY, sliceX + sliceW, sliceY + sliceH))

          # trim transparent pixels
          sprite = sprite.crop(sprite.getbbox())

          # make square
          if sprite.width != sprite.height:
            cropSize = sprite.width if sprite.width < sprite.height else sprite.height
            padSize = sprite.width if sprite.width > sprite.height else sprite.height
            if not nopad:
              sprite = ImageOps.pad(sprite, (padSize, padSize))
            else:
              adjustX = math.floor((sprite.width - cropSize) / 2)
              if alignX == "left":
                adjustX = 0
              elif alignX == "right":
                adjustX = sprite.width - cropSize
              adjustY = math.floor((sprite.height - cropSize) / 2)
              if alignY == "top":
                adjustY = 0
              elif alignY == "bottom":
                adjustY = sprite.height - cropSize
              sprite = sprite.crop((adjustX, adjustY, adjustX + cropSize, adjustY + cropSize))

          # scale down to 30x30 to fit inside border
          if sprite.width > 30:
            if not noscale:
              sprite = sprite.resize((30, 30), Image.BICUBIC)
            else:
              adjustX = math.floor((sprite.width - 30) / 2)
              if alignX == "left":
                adjustX = 0
              elif alignX == "right":
                adjustX = sprite.width - 30
              adjustY = math.floor((sprite.height - 30) / 2)
              if alignY == "top":
                adjustY = 0
              elif alignY == "bottom":
                adjustY = sprite.height - cropSize
              sprite = sprite.crop((adjustX, adjustY, adjustX + 30, adjustY + 30))

          # pad to center & fit to 32x32
          if sprite.width < 32:
            sprite = ImageOps.pad(sprite, (32, sprite.height))
          if sprite.height < 32:
            sprite = ImageOps.pad(sprite, (sprite.width, 32))

        # remove semi-transparent pixels
        alpha = sprite.getchannel("A").point(lambda p: p > 128 and 255)
        sprite.putalpha(alpha)

        tileImage.paste(sprite, None, sprite)
        sprite.close()
      else:
        tileImage = tile_unused.copy()

      tileSetImage.paste(tileImage, (offsetX, offsetY))
      tileImage.close()

  return tileSetImage

## Exports & saves image to disk.
#
#  @param filepath
#      Target output filename.
#  @param image
#      Image data to be exported.
def writeTileSet(filepath, image):
  # convert to indexed color & save
  image.convert("P").save(filepath)
  image.close()


# --- ITEMS --- #

## Creates logic tiles image for items.
#
#  @param tileSet
#      Tileset filename.
#  @param items
#      Items definitions.
def buildItemTiles(tileSet, items):
  global dir_target_item, dir_sprite_item

  print("\nCreating item tileset \"{}\" for items:".format(tileSet))
  tiles = {}
  for itemName in tuple(sorted(items.keys())):
    print("  " + itemName)
    tiles[items[itemName]["tileid"]] = itemName

  tileSetImage = buildTileSet(tiles, dir_sprite_item, items)
  if not tileSetImage:
    return
  writeTileSet(os.path.join(dir_target_item, tileSet), tileSetImage)


item_tilesets = {}

for xml in os.listdir(dir_config_item):
  lines = readConfig(os.path.join(dir_config_item, xml))
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
      tileId = -1
      for attr in line.rstrip("/>").rstrip("></type>").split(" ")[1:]:
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
        itemData["tileid"] = tileId

    if line == "</item>":
      # add to item list
      if itemName and tileSet and itemData:
        if tileSet not in item_tilesets:
          item_tilesets[tileSet] = {}
        item_tilesets[tileSet][itemName] = itemData
      itemName = None
      tileSet = None
      itemData = {}
      continue

for tileSet in item_tilesets:
  buildItemTiles(tileSet, item_tilesets[tileSet])


# --- CREATURES --- #

## Creates logic tiles image for creatures.
#
#  @param tileSet
#      Tileset filename.
#  @param creatures
#      creatures definitions.
def buildCreatureTiles(tileSet, creatures):
  global dir_target_creature, dir_sprite_creature

  print("\nCreating creature tileset \"{}\" for creatures:".format(tileSet))
  tiles = {}
  for creatureName in tuple(sorted(creatures.keys())):
    print("  " + creatureName)
    tiles[creatures[creatureName]["tileid"]] = creatureName

  tileSetImage = buildTileSet(tiles, dir_sprite_creature, creatures, False)
  if not tileSetImage:
    return
  writeTileSet(os.path.join(dir_target_creature, tileSet), tileSetImage)

creature_tilesets = {}

for xml in os.listdir(dir_config_creature):
  lines = readConfig(os.path.join(dir_config_creature, xml))
  creatureName = None
  tileSet = None
  creatureData = {}
  for line in lines:
    line = line.strip()
    if not creatureName and line.startswith("<creature name=\""):
      creatureName = line.split("\"")[1]
      continue

    if not creatureName:
      continue

    if line.startswith("<type "):
      spritePath = ""
      tileId = -1
      for attr in line.rstrip("/>").rstrip("></type>").split(" ")[1:]:
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
        creatureData["image"] = spritePath
        creatureData["tileid"] = tileId

    if line == "</creature>":
      # add to creature list
      if creatureName and tileSet and creatureData:
        if tileSet not in creature_tilesets:
          creature_tilesets[tileSet] = {}
        creature_tilesets[tileSet][creatureName] = creatureData
      creatureName = None
      tileSet = None
      creatureData = {}
      continue

for tileSet in creature_tilesets:
  buildCreatureTiles(tileSet, creature_tilesets[tileSet])

# cleanup
tile_unused.close()
tile_bg.close()
