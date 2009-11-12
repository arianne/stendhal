#!/bin/sh
STENDHAL_VERSION="0.79"

LOCALCLASSPATH=.:data/script/:data/conf/:stendhal-server-$STENDHAL_VERSION.jar:marauroa.jar:mysql-connector.jar:log4j.jar:commons-lang.jar

java -Xmx400m -cp "${LOCALCLASSPATH}" marauroa.server.marauroad -c server.ini -l

