# 文件名：Dockerfile
FROM ubuntu:22.04

# 避免交互式安裝卡住
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
    nodejs \
    npm \
    yarn \
    nano \
    vim \
    && apt-get clean

# 建立資料夾
RUN mkdir -p /cbioportal/frontend /cbioportal/backend

# 設定環境變數
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$PATH:/usr/lib/jvm/java-21-openjdk-amd64/bin

# 預設工作目錄
WORKDIR /cbioportal
