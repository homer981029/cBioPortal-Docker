FROM ubuntu:22.04

# 避免交互式介面卡住
ENV DEBIAN_FRONTEND=noninteractive

# 安裝系統依賴與基本工具
RUN apt-get update && apt-get install -y \
    curl \
    git \
    unzip \
    software-properties-common \
    build-essential \
    openjdk-21-jdk \
    maven \
    nano \
    vim \
    ca-certificates

# 安裝 nvm (Node Version Manager)
ENV NVM_DIR=/root/.nvm
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash && \
    . "$NVM_DIR/nvm.sh" && \
    nvm install 15.2.1 && \
    nvm use 15.2.1 && \
    nvm alias default 15.2.1 && \
    npm install -g yarn@1.22.5

# 建立 node/yarn 全域可用的 symlink
RUN ln -s /root/.nvm/versions/node/v15.2.1/bin/node /usr/bin/node && \
    ln -s /root/.nvm/versions/node/v15.2.1/bin/npm /usr/bin/npm && \
    ln -s /root/.nvm/versions/node/v15.2.1/bin/yarn /usr/bin/yarn

# 設定 Java 環境變數
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$PATH:/usr/lib/jvm/java-21-openjdk-amd64/bin


# 建立前後端資料夾
RUN mkdir -p /cbioportal/frontend /cbioportal/backend

# 設定預設工作路徑
WORKDIR /cbioportal

