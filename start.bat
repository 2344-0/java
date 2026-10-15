@echo off
chcp 936 >nul
setlocal enabledelayedexpansion
cd /d %~dp0

echo ============================================
echo   斗破苍穹 全栈项目 启动脚本
echo   后端 8081 ^| 前端 5173 ^| MySQL doupo
echo ============================================

:: 0) 定位 mysql 客户端（默认不在 PATH，回退到常见安装路径）
set MYSQL_BIN=mysql
where mysql >nul 2>nul
if errorlevel 1 (
  set MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
)

:: 1) 初始化数据库（幂等，每次都执行，安全；target/node_modules 被清也不影响）
echo [1/3] 初始化数据库 doupo ...
%MYSQL_BIN% -uroot -p123456 < springBootTest\db\init.sql
if errorlevel 1 (
  echo [错误] 数据库初始化失败，请确认 MySQL 已启动且 root 密码为 123456
  pause
  exit /b 1
)
echo       数据库就绪。

:: 2) 选择 Maven 命令：优先系统 mvn，否则用项目内 mvnw
set MVN=mvnw.cmd
where mvn >nul 2>nul
if not errorlevel 1 ( set MVN=mvn )

:: 3) 启动后端（新窗口；target 被清空会自动重新编译）
echo [2/3] 启动后端 http://localhost:8081 ...
start "doupo-backend" cmd /k "cd /d %~dp0springBootTest && call %MVN% spring-boot:run"

:: 4) 启动前端（新窗口；node_modules 被清空会先 npm install）
echo [3/3] 启动前端 http://localhost:5173 ...
start "doupo-frontend" cmd /k "cd /d %~dp0vue && call npm install && call npm run dev"

echo.
echo 已全部启动。后端首次编译需要几十秒，请稍候再打开 http://localhost:5173
echo 停止项目请运行 stop.bat
pause
