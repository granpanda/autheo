cd ..
jar cf dist/autheoClient.jar src/main/java/gp/e3/autheo/client/
cd dist/
mvn install:install-file -Dfile=autheoClient.jar -DgroupId=gp.e3.autheo.client -DartifactId=client -Dversion=0.1 -Dpackaging=jar
