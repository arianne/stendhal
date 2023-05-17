#!/usr/bin/env python3

import os
import shutil

import mkdocs

from mkdocs.commands    import build
from mkdocs.config.base import load_config


dir_mkdocs = os.path.dirname(__file__)
dir_root = os.path.normpath(os.path.join(dir_mkdocs, "../../"))
dir_docs = os.path.normpath(os.path.join(dir_root, "doc/mkdocs"))
dir_build = os.path.join(dir_root, "build")
dir_out = os.path.join(dir_build, "mkdocs")
dir_stage = "{}_stage".format(dir_out)
dir_javadocs = os.path.join(dir_build, "javadocs")

os.chdir(dir_root)

print("\nPreparing stage ...")

# clean up old output directory
shutil.rmtree(dir_out, True)
# in case stage dir failed to delete previously
shutil.rmtree(dir_stage, True)

# copy files to stage
shutil.copytree(dir_docs, dir_stage)

dir_img = os.path.join(dir_stage, "img")
os.makedirs(dir_img)
shutil.copy(os.path.join(dir_mkdocs, "favicon.ico"), dir_img)

if os.path.isdir(dir_javadocs):
  print("\nCopying javadocs ...")
  shutil.copytree(dir_javadocs, os.path.join(dir_stage, "java"))

os.chdir(dir_mkdocs)

print("\nGenerating documentation with mkdocs ...")

# workaround to trick mkdocs so that "docs_dir" can be set here instead of in config
if not os.path.isdir("docs"):
  os.makedirs("docs")

config = load_config()
config.site_dir = dir_out
config.docs_dir = dir_stage

# workaround to use a custom theme & fallback to default if unavailable
if "custom_theme" in config:
  theme = mkdocs.theme.Theme()
  theme.logo = None
  theme_def = config["custom_theme"]
  if type(theme_def) == dict:
    if "name" not in theme_def:
      raise ValueError("missing 'name' value in 'custom_theme'")
    theme.name = theme_def["name"]
    if "logo" in theme_def:
      theme.logo = theme_def["logo"]
  else:
    theme.name = theme_def

  try:
    theme._load_theme_config(theme.name)
    config.theme = theme
  except KeyError:
    print("WARNING: '{}' theme unavailable, falling back to '{}'".format(theme.name,
        config.theme.name))
  except Exception as e:
    print("WARNING: '{}' theme broken ({}), falling back to '{}'".format(theme.name,
        e.__class__.__name__, config.theme.name))

build.build(config)

# clean up
shutil.rmtree("docs", True)

os.chdir(dir_root)

# clean up
print("\nCleaning up ...")
shutil.rmtree(dir_stage, True)
