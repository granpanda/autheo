cd ..
java -Dfile.encoding=UTF-8 -jar -Xms50m -Xmx50m -XX:MaxPermSize=50m target/autheo-1.0-SNAPSHOT.jar server autheo-develop.yml &
