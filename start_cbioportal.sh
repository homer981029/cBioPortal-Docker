#!/bin/bash

echo "[1/4] 解壓縮 seed-db/cBioPortalSql.zip..."
cd seed-db || { echo "seed-db 資料夾不存在"; exit 1; }

unzip -o cBioPortalSql.zip -d temp
mv temp/* ./
rm -r temp

cd .. || exit

echo "[2/4] 建立 Docker 映像..."
docker compose build

echo "[3/4] 啟動 Docker 容器 (背景執行)..."
docker compose up -d

echo "[4/4] 完成！cBioPortal 應該正在啟動中。"
