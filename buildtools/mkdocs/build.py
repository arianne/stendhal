#!/usr/bin/env python3

import os
import shutil

import mkdocs

from mkdocs.commands    import build
from mkdocs.config.base import load_config


# directory where main script & MkDocs config are located
dir_mkdocs = os.path.dirname(__file__)
# Stendhal source root directory
dir_root = os.path.join(dir_mkdocs, os.path.normpath("../../"))
# documentation templates root directory
dir_templates = os.path.join(dir_mkdocs, "templates")
# build output directory
dir_build = os.path.join(dir_root, "build")
# documentation output directory
dir_out = os.path.join(dir_build, os.path.normpath("build_docs/reference"))

# change to MkDocs directory for execution
os.chdir(dir_mkdocs)

print("\nConfiguring MkDocs ...")

# workaround to trick mkdocs so that "docs_dir" can be set here instead of in config
if not os.path.isdir("docs"):
  os.makedirs("docs")

config = load_config()
config.site_dir = dir_out
config.docs_dir = dir_templates

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

print("\nGenerating Lua HTML documentation ...")
build.build(config)
# use Stendhal favicon
shutil.copy(os.path.join(dir_mkdocs, "favicon.ico"), os.path.join(dir_out, "img"))

# clean up
print("\nCleaning up ...")
shutil.rmtree("docs", True)

os.chdir(dir_root)
