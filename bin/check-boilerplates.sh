#!/usr/bin/env bash

# This script is licensed under Creative Commons Zero (CC0 / Public Domain).

# source root should be directory above script location
cd "$(dirname $0)/../"
root=$(pwd)

script="$(basename $0)";
cur_year=$(date +"%Y")

args=("$@")
write=0
if [ "${args[0]}" == "-w" ] || [ "${args[0]}" == "--write" ]; then
    write=1
    unset args[0]
    # shift so args[0] isn't undefined
    args=("${args[@]}")
fi

if [ ! -z ${args[0]} ]; then
	src_dirs=("${args[@]}")
else
	src_dirs=(
		"src/games/stendhal/client"
		"src/games/stendhal/common"
		"src/games/stendhal/server"
		"src/games/stendhal/tools"
		"src/js/stendhal"
		"data/script"
	)
fi

if [ ${write} -eq 0 ]; then
	echo -e "\nScan only mode, to update files use '${script} --write'"
fi

warnings=0
# logs a warning message
warn() {
	w="WARNING: ${1}"
	if [[ ${warnings} -eq 0 ]]; then
		# no previous output from this so erase contents
		echo -e "Issues reported by ${script}:\n${w}" > "issues.txt"
	else
		# append
		echo -e "${w}" >> "issues.txt"
	fi
	echo "${w}"
	warnings=$(expr ${warnings} + 1)
}

# logs an error message & exits
err() {
	c=${1}
	e="ERROR:   ${2} (error code: ${c}), exiting ..."
	if [[ ${warnings} -eq 0 ]]; then
		# no previous output from this run so erase contents
		echo -e "Issues reported by ${script}:\n${e}" > "issues.txt"
	else
		# append
		echo -e "${e}" >> "issues.txt"
	fi
	echo -e "${e}\nDetails saved to issues.txt"
	exit ${c}
}

start_time=$(date +"%s")

file_types=(-name "*.java" -o -name "*.js" -o -name "*.ts" -o -name "*.lua")
checked=0
updated=0
for rel_path in "${src_dirs[@]}"; do
	if [[ "${rel_path}" == /* ]]; then
		abs_path="${rel_path}"
	else
		abs_path="${root}/${rel_path}"
	fi
	if [ ! -d "${abs_path}" ]; then
		warn "Skipped missing directory: ${rel_path}"
		continue
	fi
	if [ ${write} -eq 0 ]; then
		echo -e "\nChecking copyrights in ${rel_path} ..."
	else
		echo -e "\nChecking and updating copyright year (${cur_year}) in ${rel_path} ..."
	fi

	sources=$(find "${rel_path}" -type f \( "${file_types[@]}" \))
	for src in ${sources}; do
		pattern="Copyright\( ©\)\? "
		instances=$(grep "^ \*.*${pattern}" "${src}")
		icount=$(echo "${instances}" | wc -l)

		if [ ${icount} -gt 1 ]; then
			warn "Multiple copyrights: ${src}"
			# use first instance
			instances=$(echo "${instances}" | head -n1)
		fi

		year=$(echo "${instances}" | sed -e "s/^ \*.*${pattern}//" | cut -b1-4)
		if [ "${year}" == "" ]; then
			warn "Copyright not detected: ${src}"
			continue
		elif [ "${year}" == "${cur_year}" ]; then
			years="${cur_year}"
		else
			years="${year}-${cur_year}"
		fi

		if [ ${#year} -lt 4 ]; then
			warn "Invalid year ${year}: ${src}"
		fi

		if [ ${write} -gt 0 ]; then
			# add updated year(s) to file (replace only first instance)
			sed -i -e "0,/${pattern}${year}[^ ]* \([^*]*\)\*/s//Copyright\1 ${years} \2\*/" "${src}"
			# update pattern for perl regex
			#pattern=$(echo ${pattern} | perl -pe 's/\\//g')
			#perl -i -pe "!\${i} && s/${pattern} ${year}.*? (.*)\*/Copyright\$1 ${years} \$2\*/ && (\${i}=1)" "${src}"

			ret=$?
			if [ ${ret} -ne 0 ]; then
				err ${ret} "An unhandled exception occurred"
			fi

			updated=$(expr ${updated} + 1)
		fi

		checked=$(expr ${checked} + 1)
	done
done

s=$(expr $(date +"%s") - ${start_time})
m=$(expr ${s} / 60)
s=$(expr ${s} - ${m} \* 60)
h=$(expr ${m} / 60)
m=$(expr ${m} - ${h} \* 60)

echo -e "\nDone (elapsed time: ${h}h ${m}m ${s}s)"
echo "Files checked: ${checked}"
echo "Files updated: ${updated}"
echo "Warnings: ${warnings}"
if [ ${warnings} -gt 0 ]; then
	echo "Details saved to issues.txt"
fi
