FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    curl \
    git \
    unzip \
    jq \
    software-properties-common \
    build-essential \
    openjdk-21-jdk \
    maven \
    nano \
    vim \
    ca-certificates \
    net-tools

# 設定 Java 環境變數
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# 安裝 NVM 與 Node.js 15.2.1 + Yarn 1.22.5
ENV NVM_DIR=/root/.nvm
ENV NODE_VERSION=15.2.1
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash && \
    . "$NVM_DIR/nvm.sh" && \
    nvm install $NODE_VERSION && \
    nvm alias default $NODE_VERSION && \
    npm install -g yarn@1.22.5

# symlink 建立 node/yarn 全域可用
RUN ln -s /root/.nvm/versions/node/v$NODE_VERSION/bin/node /usr/bin/node && \
    ln -s /root/.nvm/versions/node/v$NODE_VERSION/bin/npm /usr/bin/npm && \
    ln -s /root/.nvm/versions/node/v$NODE_VERSION/bin/yarn /usr/bin/yarn
RUN git config --global --add safe.directory /cbioportal

# 建立前後端資料夾
RUN mkdir -p /cbioportal/frontend /cbioportal/backend
WORKDIR /cbioportal

