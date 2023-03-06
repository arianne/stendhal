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

try:
  from PIL import Image, ImageDraw, ImageOps
except ModuleNotFoundError:
  print("ERROR: please install Python Imaging Library (PIL)")
  quit(1)


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
_file_nouse = os.path.normpath(os.path.join(dir_sprite_item, "meta/logic_nouse.png"))
tile_nouse = Image.open(_file_nouse) if os.path.isfile(_file_nouse) else Image.new("RGB", (32, 32), (255, 0, 0))

tile_bg = Image.new("RGB", (32, 32))
# draw white background image with black border
ImageDraw.Draw(tile_bg).rectangle(((0, 0), (31, 31)), (255, 255, 255), (0, 0, 0))
_file_bg_checkered = os.path.normpath(os.path.join(dir_logic, "layer/bg_checkered.png"))
tile_bg_checkered = Image.open(_file_bg_checkered).convert("RGB") if os.path.isfile(_file_bg_checkered) else tile_bg.copy()
tilesPerRow = 8


warn_count = 0
err_count = 0

def message(mtype, text=None):
  global warn_count, err_count

  if text == None:
    text = mtype
    mtype = "info"

  mtype = mtype.lower()
  if mtype == "warn":
    text = "WARNING: " + text
    warn_count += 1
  elif mtype == "error":
    text = "ERROR: " + text
    err_count += 1

  print(text)


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
creature_conf = {"groups": {}}
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
      message("warn", "empty key in creatures.conf")
      continue
    if not value:
      message("warn", "empty value for \"{}\" in creatures.conf".format(key))
      continue

    conf = {}
    group_name = None
    if key.startswith("group_"):
      group_name = key[key.index("_")+1:]
      if group_name not in creature_conf["groups"]:
        creature_conf["groups"][group_name] = {}
      conf = creature_conf["groups"][group_name]
    else:
      if key not in creature_conf:
        creature_conf[key] = {}
      conf = creature_conf[key]

    if "flags" not in conf:
      conf["flags"] = []

    for v in value.split(";"):
      v = v.strip()
      if "=" in v:
        tmp = v.split("=")
        conf[tmp[0].strip()] = tmp[1].strip()
      else:
        conf["flags"].append(v)

    if group_name:
      creature_conf["groups"][group_name] = conf
    else:
      creature_conf[key] = conf


## Builds an image from tiles data.
#
#  @param tiles
#      Tiles defintions.
#  @param spriteDir
#      Root path of sprite images tree.
#  @param defs
#      Entities or items definitions.
#  @param flags
#  @return
#      Generated tileset image.
def buildTileSet(tiles, spriteDir, defs, flags=()):
  global tile_unused, tile_nouse, tile_bg, tile_bg_checkered, tilesPerRow, creature_conf, dir_logic

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

  bgTile = tile_bg
  if "checkered" in flags:
    bgTile = tile_bg_checkered

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
      tileImage = bgTile.copy()
      text = None
      if tileId in tiles:
        name = tiles[tileIdx]
        refit = "creature_type" in flags
        sprite = None
        if name == "_nouse":
          refit = False
          sprite = tile_nouse.copy().convert("RGBA")
        elif name == "_unknown":
          refit = False
          text = "unknown"
        else:
          spritePath = os.path.normpath(os.path.join(spriteDir, defs[name]["image"]))
          sprite = Image.open(spritePath).convert("RGBA")

        if refit:
          conf = {"flags": []} if name not in creature_conf else creature_conf[name]
          nofit = "nofit" in conf["flags"]
          nopad = "nopad" in conf["flags"]
          alignX = "center" if "alignX" not in conf else conf["alignX"]
          alignY = "top" if "alignY" not in conf else conf["alignY"]
          if "text" in conf:
            text = conf["text"]

          if "downscale" in conf:
            dfactor = 1
            try:
              dfactor = int(conf["downscale"])
            except ValueError:
              message("error", "invalid tile ID: \"{}\"".format(value))
            newWidth = math.floor(sprite.width / dfactor)
            newHeight = math.floor(sprite.height / dfactor)
            sprite = sprite.resize((newWidth, newHeight), Image.BICUBIC)

          sliceX = 1
          sliceY = 2
          try:
            if "sliceX" in conf:
              sliceX = int(conf["sliceX"])
            if "sliceY" in conf:
              sliceY = int(conf["sliceY"])
          except ValueError:
            message("error", "\"slice\" parameter must be an integer")

          sliceW = math.floor(sprite.width / 3)
          sliceH = math.floor(sprite.height / 4)
          sliceX = sliceW * sliceX
          sliceY = sliceH * sliceY
          sprite = sprite.crop((sliceX, sliceY, sliceX + sliceW, sliceY + sliceH))

          # trim transparent pixels
          if "notrim" not in conf["flags"]:
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
            if not nofit:
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

        if sprite:
          # remove semi-transparent pixels
          # ~ alpha = sprite.getchannel("A").point(lambda p: p > 128 and 255)
          channels = sprite.split()
          if len(channels) > 3:
            alpha = channels[3].point(lambda p: p > 128 and 255)
            sprite.putalpha(alpha)

          tileImage.paste(sprite, None, sprite)
          sprite.close()
      else:
        tileImage = tile_unused.copy()

      if text:
        _file_text = os.path.normpath(os.path.join(dir_logic, "layer/text/{}.png".format(text)))
        if not os.path.isfile(_file_text):
          message("warn", "text image not found: {}".format(_file_text))
        else:
          tile_text = Image.open(_file_text).convert("RGBA")
          tileImage.paste(tile_text, None, tile_text)

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
  image.convert("RGB").save(filepath)
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
            message("warn", "invalid tile ID: \"{}\"".format(value))

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
  global dir_target_creature, dir_sprite_creature, creature_conf

  print("\nCreating creature tileset \"{}\" for creatures:".format(tileSet))
  tiles = {}
  for creatureName in tuple(sorted(creatures.keys())):
    print("  " + creatureName)
    tiles[creatures[creatureName]["tileid"]] = creatureName

  flags = []
  group_name = tileSet.split(".png")[0]
  if group_name in creature_conf["groups"]:
    gconf = creature_conf["groups"][group_name]
    flags = gconf["flags"]
    if "nouse" in gconf:
      try:
        for nid in gconf["nouse"].split(","):
          tiles[int(nid)] = "_nouse"
      except ValueError:
        message("error", "\"nouse\" value must be a list of integers")
    if "unknown" in gconf:
      try:
        for nid in gconf["unknown"].split(","):
          tiles[int(nid)] = "_unknown"
      except ValueError:
        message("error", "\"unknown\" value must be a list of integers")

  flags.append("creature_type")
  tileSetImage = buildTileSet(tiles, dir_sprite_creature, creatures, tuple(flags))
  if not tileSetImage:
    return
  writeTileSet(os.path.join(dir_target_creature, tileSet), tileSetImage)

creature_tilesets = {}

for xml in os.listdir(dir_config_creature):
  lines = readConfig(os.path.join(dir_config_creature, xml))
  creatureName = None
  tileSet = None
  creatureData = {}
  conf = {}
  for line in lines:
    line = line.strip()
    if not creatureName and line.startswith("<creature name=\""):
      creatureName = line.split("\"")[1]
      if creatureName in creature_conf:
        conf = creature_conf[creatureName]
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
        elif key == "subclass":
          if "subclass" in conf:
            # custom image
            value = conf["subclass"]
          if spritePath:
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
            message("warn", "invalid tile ID: \"{}\"".format(value))

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
      conf = {}
      continue

for tileSet in creature_tilesets:
  buildCreatureTiles(tileSet, creature_tilesets[tileSet])

# cleanup
tile_unused.close()
tile_nouse.close()
tile_bg.close()
tile_bg_checkered.close()

print()
print("{} warnings".format(warn_count))
print("{} errors".format(err_count))
