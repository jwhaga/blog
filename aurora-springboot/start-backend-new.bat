@echo off
cd /d "D:\0trea-make-app\?????\???\aurora-springboot"
start /B java -jar target\aurora-springboot-0.0.1.jar --spring.profiles.active=dev --SERVER_PORT=8089 > "D:\0trea-make-app\?????\???\aurora-springboot\backend-stdout.log" 2> "D:\0trea-make-app\?????\???\aurora-springboot\backend-stderr.log"
exit /b 0
