#!/bin/sh
ant compile_stendhaltools server_build
#
# log4j wants a proper configuration file log4j.properties in the classpath
# here one is provided by appending data/conf directory.
# set log4j.appender.File.File to a path with the right write permissions or an error will occur
#
#
# run:
# sh ./runBalanceRPGame.sh
# sh ./runBalanceRPGame.sh <creature>
# 
# CAVEAT: it currently doesn't handle <creature> like 'killer bat' or 'giant spider'
LOCALCLASSPATH=build/build_stendhaltools:build/build_server:build/build_server_maps:libs/marauroa.jar:libs/log4j.jar:libs/commons-lang.jar:libs/groovy.jar:libs/mysql-connector-java-5.1.5-bin.jar:libs/h2.jar:libs/swing-layout.jar:build/lib:data/conf:.
java -Dlog.directory=log -cp "${LOCALCLASSPATH}" games.stendhal.tools.BalanceRPGame "$@"
