cd seed-db/
unzip cBioPortalSql.zip -d temp
mv temp/* ./ 
rm -r temp   
cd ..

docker compose build 

docker compose up -d

#前後端
docker exec -it cbioportal-dev bash

cd backend/cbioportal/

#啟動後端
java -jar target/cbioportal-exec.jar --authenticate=false

#編譯
mvn clean install -DskipTests
