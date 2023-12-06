#!/bin/sh

STENDHAL_VERSION="1.44.5"
SERVER_JAR="stendhal-server-${STENDHAL_VERSION}.jar"

# change to server directory
cd "$(dirname $0)"

# parse arguments
vm_args=()
app_args=()
for arg in $@; do
	if [[ "${arg}" =~ ^-D ]]; then
		vm_args+=("${arg}")
	else
		app_args+=("${arg}")
	fi
done

if [ -f "${SERVER_JAR}" ]; then
	echo "Executing server from .jar ..."

	java -Xmx400m ${vm_args[@]} -jar "${SERVER_JAR}" ${app_args[@]}
else
	echo "Executing server from source root ..."

	LOCALCLASSPATH=".:./libs/*:./build/build_server:./build/build_server_maps:./build/build_server_script:./build/build_server_xmlconf"
	java -Xmx400m -cp "${LOCALCLASSPATH}" ${vm_args[@]} games.stendhal.server.StendhalServer ${app_args[@]}
fi
