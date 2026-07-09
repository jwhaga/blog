@echo off
cd /d "D:\0trea-make-app\?????\???\aurora-springboot"
start /B java -jar target\aurora-springboot-0.0.1.jar --spring.profiles.active=dev > backend-stdout.log 2> backend-stderr.log
exit /b 0
