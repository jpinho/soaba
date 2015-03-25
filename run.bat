#!/bin/sh
PATH=$PATH:/bin
mvn -q clean install -DskipTests
./client/mongoose &
mvn -q  exec:java -Dexec.mainClass="soaba.services.RestletServer"
