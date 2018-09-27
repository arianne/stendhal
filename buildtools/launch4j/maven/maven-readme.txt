Run once the Ant target switch-to-maven, it will reconfigure the project to use maven.

*** Creation of Distribution Release ***
Windows: mvn -P dist,win32 clean package

Linux: mvn -P dist,linux clean package  

MacOsX: mvn -P dist,macosx-x86 clean package
