cd seed-db/
unzip cBioPortalSql.zip -d temp
mv temp/* ./ 
rm -r temp   
cd ..

docker compose build 

docker compose up -d

#前後端
docker exec -it cbioportal-dev bash

#切資料夾
cd backend/cbioportal/

#啟動後端
java -jar target/cbioportal-exec.jar --authenticate=false

#編譯
# mvn clean install -DskipTests

#前端 切資料夾

docker exec -it cbioportal-frontend bash

#切資料夾
cd frontend

#安裝依賴
yarn install --frozen-lockfile

yarn run buildDLL:dev
yarn run buildModules
yarn run start


