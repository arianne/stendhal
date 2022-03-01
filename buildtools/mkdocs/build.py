#!/usr/bin/env python

import os, shutil

from mkdocs.commands import build
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
shutil.copy("README.md", dir_stage)
shutil.copy("LICENSE.txt", dir_stage)

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
config["site_dir"] = dir_out
config["docs_dir"] = dir_stage

build.build(config)

# clean up
shutil.rmtree("docs", True)

os.chdir(dir_root)

# clean up
print("\nCleaning up ...")
shutil.rmtree(dir_stage, True)
