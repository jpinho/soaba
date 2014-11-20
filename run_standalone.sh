mvn -q clean verify -Dmaven.test.skip=true
mvn -q exec:java -Dexec.mainClass="soaba.services.RestletServer"