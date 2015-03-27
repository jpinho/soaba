mvn -q clean install -DskipTests
START /b client\mongoose.exe 
START /b mvn -q exec:java -Dexec.mainClass="soaba.services.RestletServer"
