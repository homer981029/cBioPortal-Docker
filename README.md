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


------//////////////////////////////////////////////
[#/backend/cbioportal/src/main/java/org/cbioportal/legacy/web/config -M]
add file:

CLASS NAME:WebConfig.java

package org.cbioportal.legacy.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
             @Override
             public void addCorsMappings(CorsRegistry registry) {
                 registry.addMapping("/api/**")
                         .allowedOrigins("http://localhost:3000", "http://192.168.168.117:3000")
                         .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                         .allowedHeaders("*")
                         .allowCredentials(true);
             }
        };
    }
}

------//////////////////////////////////////////////
[#cbioportal/src/main/java/org/cbioportal/legacy/web/PublicVirtualStudiesController.java -M]

add:
public ResponseEntity<List<VirtualStudy>> getPublicVirtualStudies() {
    try {
        List<VirtualStudy> virtualStudies =
            sessionServiceRequestHandler.getVirtualStudiesAccessibleToUser(ALL_USERS);
        return new ResponseEntity<>(virtualStudies, HttpStatus.OK);
    } catch (Exception e) {
        // 改為回傳空陣列以避免前端 HTTP 500
        return new ResponseEntity<>(List.of(), HttpStatus.OK);
    }
}
remove:
  // public ResponseEntity<List<VirtualStudy>> getPublicVirtualStudies() {
  //   List<VirtualStudy> virtualStudies =
  //       sessionServiceRequestHandler.getVirtualStudiesAccessibleToUser(ALL_USERS);
  //   return new ResponseEntity<>(virtualStudies, HttpStatus.OK);
  // }

------//////////////////////////////////////////////
[#/backend/cbioportal/config/application.properties -M]

#11~12line:
# ✅ 關閉驗證機制，讓 portal 可正常開啟而不要求登入
portal.authenticate=false

#14~20line:
add:
# database
# Properties for when Clickhouse mode is enabled (Warning: Experimental)
#spring.datasource.url=jdbc:mysql://mysql:3306/cbioportal?useSSL=false&allowPublicKeyRetrieval=true
#spring.datasource.username=cbio
#spring.datasource.password=P@ssword1
#spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
#spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
remove:
# database
# Properties for when Clickhouse mode is enabled (Warning: Experimental)
#spring.datasource.mysql.url=jdbc:mysql://localhost:3306/cbioportal?useSSL=false
#spring.datasource.mysql.username=cbio
#spring.datasource.mysql.password=P@ssword1
#spring.datasource.mysql.driver-class-name=com.mysql.jdbc.Driver
#spring.datasource.clickhouse.url=jdbc:ch://localhost:8443/cbioportal
#spring.datasource.clickhouse.username=dummy
#spring.datasource.clickhouse.password=dummy
#spring.datasource.clickhouse.driver-class-name=com.clickhouse.jdbc.ClickHouseDriver
spring.datasource.url=jdbc:mysql://localhost:3306/cbioportal?useSSL=false
spring.datasource.username=cbio
spring.datasource.password=P@ssword1

#248~254line:
add:
session_service.url=http://localhost/disabled
remote.session.service.enabled=false
disabled_tabs=bookmark
remove:
session.service.url=https://cbioportal-session-service.herokuapp.com/session_service/api/sessions/heroku_portal/

------//////////////////////////////////////////////
[#frontend/my-index.ejs -M]

add:
<script src="<%= htmlWebpackPlugin.files.js[0] %>"></script>

------//////////////////////////////////////////////
[#frontend/webpack.config.js -M]

#11~20line:
add:
remove:
// Don't show COMMIT/VERSION on Heroku (crashes, because no git dir)
if (process.env.PATH.indexOf('heroku') === -1) {
    // show full git version
    var { GitRevisionPlugin } = require('git-revision-webpack-plugin');
    var gitRevisionPlugin = new GitRevisionPlugin({
        versionCommand: 'describe --always --tags --dirty',
    });
    commit = JSON.stringify(gitRevisionPlugin.commithash());
    version = JSON.stringify(gitRevisionPlugin.version());
}

#50~56line:
add:
const devHost = process.env.PUBLIC_HOST || 'localhost'; // 給瀏覽器看的
const listenHost = process.env.HOST || '0.0.0.0'; // 給 webpack-dev-server 用
const devPort = process.env.PORT || 3000;
remove:
const devHost = process.env.HOST || 'localhost';
const devPort = process.env.PORT || 3000;

#144~158line:
add:
        new webpack.DefinePlugin({
            VERSION: JSON.stringify('dev'),
            COMMIT: JSON.stringify('local'),
            IS_DEV_MODE: isDev,
            ENV_CBIOPORTAL_URL: process.env.CBIOPORTAL_URL
                ? JSON.stringify(
                      cleanAndValidateUrl(process.env.CBIOPORTAL_URL)
                  )
                : '"http://localhost:8080"', // 預設值
            ENV_GENOME_NEXUS_URL: process.env.GENOME_NEXUS_URL
                ? JSON.stringify(
                      cleanAndValidateUrl(process.env.GENOME_NEXUS_URL)
                  )
                : '"http://localhost:8888"', // 預設值
        }),
remove:
        new webpack.DefinePlugin({
            VERSION: version,
            COMMIT: commit,
            IS_DEV_MODE: isDev,
            ENV_CBIOPORTAL_URL: process.env.CBIOPORTAL_URL
                ? JSON.stringify(
                      cleanAndValidateUrl(process.env.CBIOPORTAL_URL)
                  )
                : '"replace_me_env_cbioportal_url"',
            ENV_GENOME_NEXUS_URL: process.env.GENOME_NEXUS_URL
                ? JSON.stringify(
                      cleanAndValidateUrl(process.env.GENOME_NEXUS_URL)
                  )
                : '"replace_me_env_genome_nexus_url"',
        }),

#511~528line:
add:
    config.output.publicPath = `//${devHost}:${devPort}/`;
remove:
    config.output.publicPath = `//localhost:${devPort}/`;

#376~416line:
add:
    devServer: {
        static: {
            directory: path.resolve(__dirname, 'dist'),
        },
        hot: true,
        historyApiFallback: true,
        client: {
            overlay: {
                errors: true,
                warnings: false,
            },
            webSocketURL: `ws://${devHost}:${devPort}/ws`, // ✅ 正確替代用法
        },
        https: false,
        host: listenHost, // '0.0.0.0'
        port: devPort,
        headers: { 'Access-Control-Allow-Origin': '*' },
        allowedHosts: 'all',
        devMiddleware: {
            publicPath: `//${devHost}:${devPort}/`, // ✅ 給 HTML 用的正確輸出位址
            stats: 'errors-only',
        },
    },
remove:
    devServer: {
        static: {
            directory: path.resolve(__dirname, 'dist'),
        },
        hot: true,
        historyApiFallback: true,
        // TODO removed in favor of https://webpack.js.org/configuration/other-options/#infrastructurelogging
        // noInfo: false,
        // quiet: false,
        // lazy: false,
        client: {
            overlay: {
                errors: true,
                warnings: false,
            },
        },
        https: false,
        host: 'localhost',
        headers: { 'Access-Control-Allow-Origin': '*' },
        allowedHosts: 'all',
        devMiddleware: {
            publicPath: '/',
            stats: 'errors-only',
        },
    },









------

