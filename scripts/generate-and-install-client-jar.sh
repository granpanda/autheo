cd ..
mkdir dist
cd target/classes/
jar cf ../../dist/autheoClient.jar gp/e3/autheo/client/*
cd ../../dist/
mvn install:install-file -Dfile=autheoClient.jar -DgroupId=gp.e3.autheo.client -DartifactId=client -Dversion=0.1 -Dpackaging=jar
