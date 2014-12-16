set STENDHAL_VERSION=1.16.5
set LOCALCLASSPATH=.;data\script;data\conf;stendhal-server-%STENDHAL_VERSION%.jar;marauroa.jar;mysql-connector.jar;log4j.jar;commons-lang.jar;h2.jar
java -Xmx400m -cp "%LOCALCLASSPATH%" games.stendhal.server.StendhalServer -c server.ini -l
@pause