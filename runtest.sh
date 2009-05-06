
# before you can use this file the first time ( per session) to run a single test , you have to use ant compile_tests 

java -cp .:./:./build/build_tests/:./build/build_server/:./build/build_client/:./build/build_server_maps/:./libs/junit-4.4.jar:./libs/marauroa.jar:./libs/log4j.jar:./libs/commons-lang.jar:./libs/mysql-connector-java-5.1.5-bin.jar:./libs/hamcrest-all-1.1.jar:./libs/easymock.jar:./libs/easymockclassextension.jar:./libs/cglib-nodep-2.2_beta1.jar:./libs/pagelayout1.16.jar org.junit.runner.JUnitCore games.stendhal.server.entity.RPEntityTest


