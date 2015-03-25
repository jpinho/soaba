mvn -q clean install -DskipTests
mvn -q exec:java -Dexec.mainClass="soaba.services.RestletServer"
