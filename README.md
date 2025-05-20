#自動啟動指令
chmod +x start_cbioportal.sh
./start_cbioportal.sh


#手動啟動指令
cd seed-db/
unzip cBioPortalSql.zip -d temp
mv temp/* ./ 
rm -r temp   
cd ..

docker compose build 

docker compose up -d
------------------------------------------------------------

#前後端
docker exec -it cbioportal-dev bash

#切資料夾
cd backend/cbioportal/

#啟動後端
java -jar target/cbioportal-exec.jar --authenticate=false

============================================================

#前端 切資料夾
docker exec -it cbioportal-frontend bash

#切資料夾
cd frontend

#啟動前端
yarn run watch



#編譯jar
# mvn clean install -DskipTests


