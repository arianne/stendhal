
# before you can use this file the first time ( per session) to run a single test , you have to use ant compile_tests 

java -cp .:./:./build/build_tests/:./build/build_server/:./build/build_client/:./build/build_server_maps/:./libs/* org.junit.runner.JUnitCore games.stendhal.server.entity.RPEntityTest


