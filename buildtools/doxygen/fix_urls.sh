#!/usr/bin/env bash

# Doxygen is trimming the .html suffix from external references in tag files.
# So this script is a hack to fix URLs after HTML documentation is generated
# until a fix is determined for tag files.


cd "$(dirname $0)/../../"

IFS_orig="${IFS}"
IFS=$'\n'

echo "Caching file list ..."
file_list="$(grep -rl --include="*.html" "href=\".*/java\.base/" build/doxygen/)"
file_count=0

for html in ${file_list}; do
	file_count=$((${file_count} + 1))
done

file_idx=0
for html in ${file_list}; do
	file_idx=$((${file_idx} + 1))
	printf "Fixing URLs (file ${file_idx}/${file_count}) ...\r"
	sed -i -e 's|href="\(.*\)/java\.base/\(.*\)"|href="\1/java.base/\2.html"|g' "${html}"
done

IFS="${IFS_orig}"
