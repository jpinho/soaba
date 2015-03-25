mvn -q clean install -DskipTests
client\mongoose.exe
mvn -q  exec:java -Dexec.mainClass="soaba.services.RestletServer"