#!/usr/bin/env python

# ***************************************************************************
# *                       Copyright © 2023 - Stendhal                       *
# ***************************************************************************
# *                                                                         *
# *   This program is free software; you can redistribute it and/or modify  *
# *   it under the terms of the GNU General Public License as published by  *
# *   the Free Software Foundation; either version 2 of the License, or     *
# *   (at your option) any later version.                                   *
# *                                                                         *
# ***************************************************************************


# script for generating data/language/template.txt

import codecs
import errno
import os
import re
import sys


dir_root = os.path.dirname(os.path.dirname(__file__))
dir_config = os.path.normpath(os.path.join(dir_root, "data/conf"))
dir_locale = os.path.normpath(os.path.join(dir_root, "data/languages"))
file_template = os.path.join(dir_locale, "template.txt")


# Helper functions.

def printWarning(msg):
	sys.stdout.write("WARNING: {}\n".format(msg))

def printError(msg):
	sys.stderr.write("ERROR: {}\n".format(msg))

def exitWithError(err, msg):
	printError(msg)
	sys.exit(err)

def escapeRegex(st):
	return st.replace("*", "\\*")\
			.replace("?", "\\?")\
			.replace("+", "\\+")\
			.replace("(", "\\(")\
			.replace(")", "\\)")\
			.replace("[", "\\[")\
			.replace("]", "\\]")\
			.replace("|", "\\|")


# categories, names, & descriptions cache
__cache = {
	"items": {},
	"creatures": {}
}


## Parses configuration file for names & descriptions.
def parseConfig(_type, category):
	print("parsing {} names and descriptions from category '{}'".format(_type, category))

	dir_type = os.path.join(dir_config, _type)
	if not os.path.isdir(dir_type):
		printWarning("'{}' directory not found, excluding from translations template".format(dir_type))
		return

	file_xml = os.path.join(dir_type, category + ".xml")
	if not os.path.isfile(file_xml):
		printWarning("'{}' file not found, excluding from translation template".format(file_xml))
		return

	if category not in __cache[_type]:
		__cache[_type][category] = {}
	tmp = __cache[_type][category]

	try:
		fin = codecs.open(file_xml, "r", "utf-8")
		# ensure working with LF line endings
		content = fin.read().replace("\r\n", "\n").replace("\r", "\n")
		fin.close()
	except PermissionError:
		exitWithError(errno.EACCES, "cannot open '{}' for reading, permission denied".format(file_xml))

	# tag to parse from xml
	tag = _type
	if tag.endswith("s"):
		tag = tag[:-1]

	item_name = None
	item_desc = None
	in_comment = False
	for line in content.split("\n"):
		line = line.strip()

		# ignore commented sections
		# NOTE: only ignores lines starting with a comment and ignores content on same line after comment
		if in_comment and "-->" in line:
			in_comment = False
			continue
		if not in_comment:
			in_comment = line.startswith("<!--") and "-->" not in line
		if in_comment:
			continue

		if line.startswith("<{} name=\"".format(tag)):
			item_name = re.sub("^<{} name=\"".format(tag), "", line)
			item_name = re.sub("\".*$", "", item_name).strip()
		elif item_name and line.startswith("<description>"):
			item_desc = re.sub("^<description>", "", line)
			item_desc = re.sub("</description>$", "", item_desc).strip()
		elif item_name and line == "</{}>".format(tag):
			# there may be multiple versions of an entity/item with different descriptions
			if item_name in tmp and item_desc:
				desc_new = item_desc
				# preserve previous description(s)
				item_desc = tmp[item_name]
				# convert to a list for multiple descriptions
				if type(item_desc) == str:
					item_desc = [item_desc]
				elif not item_desc:
					item_desc = []
				# add new description
				item_desc.append(desc_new)

			tmp[item_name] = item_desc

			# reset
			item_name = None
			item_desc = None

	__cache[_type][category] = tmp


## Exports parsed data to template text file.
def exportTemplate():
	if not os.path.isdir(dir_locale):
		exitWithError(errno.ENOENT, "'{}' directory not found, cannot create translation template".format(dir_locale))

	try:
		fout = codecs.open(file_template, "w", "utf-8")
	except PermissionError:
		exitWithError(errno.EACCES, "cannot open '{}' for writing, permission denied".format(file_template))

	# main header
	header = ("", "##", "## Language: <name> (<code>)", "## Translators: <translator1>[, <translator2>...]", "##")
	fout.write("\n".join(header) + "\n")

	for _type in __cache:
		# type header
		fout.write("\n\n# {} names and descriptions".format(_type))

		contents = __cache[_type]
		for category in tuple(sorted(contents)):
			if not contents[category]:
				printWarning("'{}' category '{}' is empty, skipping".format(_type, category))
				continue

			# category header
			fout.write("\n\n## " + category + "\n")

			cat_list = contents[category]
			for item_name in cat_list:
				fout.write("\n" + item_name + "=\n")
				item_desc = cat_list[item_name]
				if item_desc:
					if type(item_desc) == str:
						item_desc = [item_desc]
					for desc in item_desc:
						fout.write(desc + "=\n")

	fout.close()

	print("template exported to '{}'".format(file_template))


## Builds a localization template for translating.
def buildTemplate():
	# categores not translated
	cat_excludes = {
		"items": ("dummy_weapons", "meta")
	}

	for _type in __cache:
		print("parsing type: {}".format(_type))

		category_names = []

		dir_type = os.path.join(dir_config, _type)
		for basename in sorted(os.listdir(dir_type)):
			# include xml configuration files only
			if not os.path.isfile(os.path.join(dir_type, basename)) or not basename.endswith(".xml"):
				continue

			# remove .xml filename suffix
			category = basename[:-4]
			if _type in cat_excludes and category in cat_excludes[_type]:
				continue
			category_names.append(category)

		if len(category_names) == 0:
			printWarning("no '{}' categories found, excluding from translation template".format(_type))

		for category in category_names:
			parseConfig(_type, category)

	exportTemplate()


## Updates existing translations found in data/languages.
def updateExisting():
	if not os.path.isdir(dir_locale):
		printWarning("directory '{}' not found, no translations available for updating".format(dir_locale))
		return
	if not os.path.isfile(file_template):
		exitWithError(errno.ENOENT, "template '{}' not found, cannot update translations".format(file_template))

	try:
		fin = codecs.open(file_template, "r", "utf-8")
		template = fin.read().replace("\r\n", "\n").replace("\r", "\n")
		fin.close()
	except PermissionError:
		exitWithError(errno.EACCES, "cannot open '{}' for reading, permission denied".format(file_template))

	locale_codes = []
	for basename in os.listdir(dir_locale):
		if not basename.endswith(".txt") or basename == "template.txt":
			continue
		locale_codes.append(basename[:-4])

	if not locale_codes:
		printWarning("no translations available for updating")
		return

	for code in locale_codes:
		print("updating locale '{}'".format(code))

		filepath = os.path.join(dir_locale, code + ".txt")
		try:
			fin = codecs.open(filepath, "r", "utf-8")
			contents_old = fin.read().replace("\r\n", "\n").replace("\r", "\n")
			fin.close()
		except PermissionError:
			exitWithError(errno.EACCES, "cannot open '{}' for reading, permission denied".format(filepath))

		# make a copy of template
		contents = template

		header_language = None
		header_translators = None
		unused = []
		lidx = 0
		for line in contents_old.split("\n"):
			lidx += 1

			# check for headers
			if not header_language and line.startswith("## Language:"):
				header_language = re.sub("^## Language:", "", line).strip()
				continue
			if not header_translators and line.startswith("## Translators:"):
				header_translators = re.sub("^## Translators:", "", line).strip()
				continue

			# ignore comments & empty lines
			if not line.strip() or line.startswith("#"):
				continue
			if "=" not in line:
				printWarning("malformed line ({}) in '{}'".format(lidx, filepath))
				continue

			key = escapeRegex(line.split("=", 1)[0]) + "="
			if not re.search("^" + key, contents, re.M):
				unused.append(line)
				continue
			contents = re.sub("^" + key + ".*$", line, contents, flags=re.M)

		if header_language:
			contents = re.sub("^## Language:.*$", "## Language: {}".format(header_language), contents, 1, re.M)
		if header_translators:
			contents = re.sub("^## Translators:.*$", "## Translators: {}".format(header_translators), contents, 1, re.M)

		contents = contents.rstrip()

		if unused:
			contents += "\n\n\n# strings not found in template\n"
			for line in unused:
				contents += "\n" + line

		try:
			fout = codecs.open(filepath, "w", "utf-8")
			fout.write(contents + "\n")
			fout.close()
		except PermissionError:
			exitWithError(errno.EACCES, "cannot open '{}' for writing, permission denied".format(filepath))

		print("exported to '{}'".format(filepath))


if __name__ == "__main__":
	# work from source root
	os.chdir(dir_root)

	args = sys.argv[1:]
	if "--update" in args:
		updateExisting()
	else:
		buildTemplate()
