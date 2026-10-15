@echo off
chcp 936 >nul
echo 正在停止 斗破苍穹 项目 ...

:: 按窗口标题结束（最稳）
taskkill /FI "WINDOWTITLE eq doupo-backend" /F >nul 2>nul
taskkill /FI "WINDOWTITLE eq doupo-frontend" /F >nul 2>nul

:: 按端口结束（兜底，防止窗口标题不匹配）
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081 ^| findstr LISTENING') do taskkill /PID %%a /F >nul 2>nul
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173 ^| findstr LISTENING') do taskkill /PID %%a /F >nul 2>nul

echo 已发送停止信号。若仍占用端口，请手动：taskkill /F /PID 对应PID
pause
