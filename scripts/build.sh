cd ..
mvn clean compile test package install
cd scripts
sh generate-and-install-client-jar.sh
