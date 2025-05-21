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

#加git 進入環境變數
export GIT_DIR=/cbioportal/.git

#切資料夾
cd backend/cbioportal/

#編譯jar (first time)
mvn clean install -DskipTests

#啟動後端
java -jar target/cbioportal-exec.jar --authenticate=false

============================================================

#前端 切資料夾
docker exec -it cbioportal-frontend bash

#切資料夾
cd frontend

#編譯dll動態庫
/root/.nvm/versions/node/v15.2.1/bin/node ./node_modules/webpack/bin/webpack.js --config vendor-bundles.webpack.config.js

#啟動專案
yarn run watch






------//////////////////////////////////////////////
[#/backend/cbioportal/src/main/java/org/cbioportal/legacy/web/config -M]-DELETE

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
[#cbioportal/src/main/java/org/cbioportal/legacy/web/PublicVirtualStudiesController.java -M] -DELETE

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
<% for (let script of htmlWebpackPlugin.files.js) { %>
  <script src="<%= script %>"></script>
<% } %>

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

------//////////////////////////////////////////////
[#package.json -M]

add:
scripts:{
    ...
    "watch": "./scripts/env_vars.sh && eval \"$(./scripts/env_vars.sh)\" && cross-env NODE_ENV=development webpack-dev-server --compress --host 0.0.0.0",
    ...
}
remove:
scripts:{
    ...
    "watch": "./scripts/env_vars.sh && eval \"$(./scripts/env_vars.sh)\" && cross-env NODE_ENV=development webpack-dev-server --compress",
    ...
}

------//////////////////////////////////////////////
[#frontend/scripts/env_vars.sh -M]

add:
export CBIOPORTAL_URL=${CBIOPORTAL_URL:-http://localhost:8080}
export GENOME_NEXUS_URL=${GENOME_NEXUS_URL:-https://www.genomenexus.org}


------//////////////////////////////////////////////
[#/frontend/packages/cbioportal-frontend-commons/src/lib/TextTruncationUtils.ts -M]

add:
import _ from 'lodash';

const canvas = document.createElement('canvas');
const context = canvas.getContext('2d')!;

function setFont(fontFamily: string, fontSize: string) {
    context.font = `${fontSize} ${fontFamily}`;
}

export function getTextWidth(text: string, fontFamily: string, fontSize: string) {
    setFont(fontFamily, fontSize);
    return context.measureText(text).width;
}

export function getTextHeight(text: string, fontFamily: string, fontSize: string) {
    // Canvas 不提供精確高度，我們保守估計為 1.2 倍 fontSize（單行）
    return parseFloat(fontSize) * 1.2;
}

export function getTextDiagonal(textHeight: number, textWidth: number) {
    return Math.sqrt(Math.pow(textWidth, 2) + Math.pow(textHeight, 2));
}

function splitTextByWidth(
    text: string,
    maxWidth: number,
    fontFamily: string,
    fontSize: string
) {
    const ret: string[] = [];
    let index = 0;
    let chunk = '';
    while (index < text.length) {
        chunk += text[index];
        if (getTextWidth(chunk, fontFamily, fontSize) >= maxWidth) {
            ret.push(chunk);
            chunk = '';
        }
        index += 1;
    }
    if (chunk.length) {
        ret.push(chunk);
    }
    return ret;
}

export function wrapText(
    text: string,
    maxWidth: number,
    fontFamily: string,
    fontSize: string
): string[] {
    if (getTextWidth(text, fontFamily, fontSize) <= maxWidth) {
        return [text];
    } else {
        let words = text.split(/\s+/g);
        words = _.flatten(
            words.map(word =>
                splitTextByWidth(word, maxWidth, fontFamily, fontSize)
            )
        );
        let currentLine = '';
        const ret: string[] = [];
        for (const word of words) {
            if (getTextWidth(word, fontFamily, fontSize) >= maxWidth) {
                if (currentLine.length) {
                    ret.push(currentLine);
                }
                ret.push(word);
                currentLine = '';
            } else if (
                getTextWidth(
                    (currentLine.length ? currentLine + ' ' : currentLine) +
                        word,
                    fontFamily,
                    fontSize
                ) >= maxWidth
            ) {
                if (currentLine.length) {
                    ret.push(currentLine);
                }
                currentLine = word;
            } else {
                currentLine += (currentLine.length ? ' ' : '') + word;
            }
        }
        if (currentLine.length) {
            ret.push(currentLine);
        }
        return ret;
    }
}

export function truncateWithEllipsis(
    text: string,
    maxWidth: number,
    fontFamily: string,
    fontSize: string
): string {
    const wrapped = splitTextByWidth(text, maxWidth, fontFamily, fontSize);
    if (wrapped.length > 1) {
        return wrapped[0] + '...';
    } else {
        return wrapped[0];
    }
}

export function truncateWithEllipsisReport(
    text: string,
    maxWidth: number,
    fontFamily: string,
    fontSize: string
) {
    const wrapped = splitTextByWidth(text, maxWidth, fontFamily, fontSize);
    let isTruncated = false;
    if (wrapped.length > 1) {
        text = wrapped[0] + '...';
        isTruncated = true;
    } else {
        text = wrapped[0];
    }
    return {
        text,
        isTruncated,
    };
}
remove:
import measureText from 'measure-text';
import _ from 'lodash';

export function getTextDiagonal(textHeight: number, textWidth: number) {
    return Math.sqrt(Math.pow(textWidth, 2) + Math.pow(textHeight, 2));
}

export function getTextHeight(
    text: string,
    fontFamily: string,
    fontSize: string
) {
    return measureText({ text, fontFamily, fontSize, lineHeight: 1 }).height
        .value;
}

export function getTextWidth(
    text: string,
    fontFamily: string,
    fontSize: string
) {
    return measureText({ text, fontFamily, fontSize, lineHeight: 1 }).width
        .value;
}

function splitTextByWidth(
    text: string,
    maxWidth: number,
    fontFamily: string,
    fontSize: string
) {
    const ret: string[] = [];
    let index = 0;
    let chunk = '';
    while (index < text.length) {
        chunk += text[index];
        if (getTextWidth(chunk, fontFamily, fontSize) >= maxWidth) {
            ret.push(chunk);
            chunk = '';
        }
        index += 1;
    }
    if (chunk.length) {
        ret.push(chunk);
    }
    return ret;
}

export function wrapText(
    text: string,
    maxWidth: number,
    fontFamily: string,
    fontSize: string
): string[] {
    if (getTextWidth(text, fontFamily, fontSize) <= maxWidth) {
        return [text];
    } else {
        // label too big, need to wrap to fit
        let words = text.split(/\s+/g); // first split words, for nicer breaks if possible
        // next split chunks of max width
        words = _.flatten(
            words.map(word =>
                splitTextByWidth(word, maxWidth, fontFamily, fontSize)
            )
        );
        let currentLine = '';
        const ret: string[] = [];
        for (const word of words) {
            if (getTextWidth(word, fontFamily, fontSize) >= maxWidth) {
                if (currentLine.length) {
                    ret.push(currentLine);
                }
                ret.push(word);
                currentLine = '';
            } else if (
                getTextWidth(
                    (currentLine.length ? currentLine + ' ' : currentLine) +
                        word,
                    fontFamily,
                    fontSize
                ) >= maxWidth
            ) {
                if (currentLine.length) {
                    ret.push(currentLine);
                }
                currentLine = word;
            } else {
                if (currentLine.length) {
                    currentLine += ' ';
                }
                currentLine += word;
            }
        }
        if (currentLine.length) {
            ret.push(currentLine);
        }
        return ret;
    }
}

export function truncateWithEllipsis(
    text: string,
    maxWidth: number,
    fontFamily: string,
    fontSize: string
): string {
    const wrapped = splitTextByWidth(text, maxWidth, fontFamily, fontSize);
    if (wrapped.length > 1) {
        return wrapped[0] + '...';
    } else {
        return wrapped[0];
    }
}

export function truncateWithEllipsisReport(
    text: string,
    maxWidth: number,
    fontFamily: string,
    fontSize: string
) {
    const wrapped = splitTextByWidth(text, maxWidth, fontFamily, fontSize);
    let isTruncated = false;
    if (wrapped.length > 1) {
        text = wrapped[0] + '...';
        isTruncated = true;
    } else {
        text = wrapped[0];
        isTruncated = false;
    }
    return {
        text,
        isTruncated,
    };
}




------

