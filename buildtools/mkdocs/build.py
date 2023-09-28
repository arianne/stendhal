#!/usr/bin/env python3

import os
import shutil

import mkdocs

from mkdocs.commands    import build
from mkdocs.config.base import load_config


# directory where main script & MkDocs config are located
dir_mkdocs = os.path.dirname(__file__)
# Stendhal source root directory
dir_root = os.path.normpath(os.path.join(dir_mkdocs, "../../"))
# documentation templates root directory
dir_templates = os.path.normpath(os.path.join(dir_mkdocs, "templates"))
# build output directory
dir_build = os.path.join(dir_root, "build")
# documentation output directory
dir_out = os.path.join(dir_build, "mkdocs")
# temporary directory for staging documentation output
dir_stage = "{}_stage".format(dir_out)
# Java documentation directory
dir_javadocs = os.path.join(dir_build, "javadocs")

# ensure working from source root directory
os.chdir(dir_root)

print("\nPreparing documentation staging directory ...")

# clean up old files
shutil.rmtree(dir_out, True)
# in case staging dir failed to delete previously
shutil.rmtree(dir_stage, True)

# copy template files to staging directory
shutil.copytree(dir_templates, dir_stage)

# create directory for image resources
dir_img = os.path.join(dir_stage, "img")
os.makedirs(dir_img)
shutil.copy(os.path.join(dir_mkdocs, "favicon.ico"), dir_img)

# copy Java documentation if available
if os.path.isdir(dir_javadocs):
  print("\nCopying javadocs to staging directory ...")
  shutil.copytree(dir_javadocs, os.path.join(dir_stage, "java"))
else:
  print("\nJavadocs directory not found, skipping ...")

# change to MkDocs directory for execution
os.chdir(dir_mkdocs)

print("\nConfiguring MkDocs ...")

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
    for key in theme_def:
      setattr(theme, key, theme_def[key])
  else:
    theme.name = theme_def
  if theme.name == None:
    raise ValueError("missing 'name' value in 'custom_theme'")

  try:
    theme._load_theme_config(theme.name)
    config.theme = theme
  except KeyError:
    print("WARNING: '{}' theme unavailable, falling back to '{}'".format(theme.name,
        config.theme.name))
  except mkdocs.config.base.ValidationError as e:
    print("WARNING: '{}' theme validation failed ({}), falling back to '{}'".format(theme.name, e,
        config.theme.name))
  except Exception as e:
    print("WARNING: '{}' theme broken ({}: {}), falling back to '{}'".format(theme.name,
        e.__class__.__name__, e, config.theme.name))

# generate HTML documentation
print("\nGenerating HTML documentation ...")
build.build(config)

# clean up
print("\nCleaning up ...")
shutil.rmtree("docs", True)
shutil.rmtree(dir_stage, True)

os.chdir(dir_root)
