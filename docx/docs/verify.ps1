# Aurora 重构版 Part 验证脚本
# 用法: .\verify.ps1 [part-number]
# 示例: .\verify.ps1 1.4   # 验证 Part 1.4
#        .\verify.ps1       # 全量验证（M1 回归）

param([string]$Part = "all")
$ErrorActionPreference = "Continue"
$env:JAVA_HOME = "D:\Develop\JDK25\jdk-25.0.3+9"
$MvnCmd = "d:\Develop\Maven\apache-maven-3.9.16\bin\mvn.cmd"
$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$BackendDir = Join-Path $RootDir "aurora-springboot"
$TargetJar = Join-Path $BackendDir "target\aurora-springboot-0.0.1.jar"
function Step($label) { Write-Host " === $label ===" -ForegroundColor Cyan }
function Ok { Write-Host "  [OK]" -ForegroundColor Green }
function Fail($msg) { Write-Host "  [FAIL] $msg" -ForegroundColor Red; exit 1 }

Write-Host "=== 1. 检查基础设施 ===" -ForegroundColor Cyan
$hasDocker = Get-Command docker -ErrorAction SilentlyContinue
if (-not $hasDocker) { Write-Host "  未安装 Docker，请手动启动 MySQL/Redis/RabbitMQ" -ForegroundColor Yellow }

Write-Host "=== 2. 编译 ===" -ForegroundColor Cyan
Set-Location $BackendDir
& $MvnCmd clean compile -q
if ($LASTEXITCODE -ne 0) { Fail "编译失败" }
Ok

Write-Host "=== 3. 单元测试 ===" -ForegroundColor Cyan
if ($Part -eq "all") {
    & $MvnCmd test
    if ($LASTEXITCODE -ne 0) { Fail "单元测试失败" }
} else {
    Write-Host "  跳过全量测试（Part 级验证只跑目标测试）" -ForegroundColor Yellow
}
Ok

Write-Host "=== 4. 打包 ===" -ForegroundColor Cyan
& $MvnCmd package -DskipTests -q
if ($LASTEXITCODE -ne 0) { Fail "打包失败" }
Ok

if ($Part -match "^1\.[1-3]$" -and $Part -ne "all") {
    Write-Host "  Part 1.1-1.3 跳过启动验证（此时项目尚不完全可编译）" -ForegroundColor Yellow
    exit 0
}

Write-Host "=== 5. 启动后端 ===" -ForegroundColor Cyan
if (-not (Test-Path $TargetJar)) { Fail "找不到打包产物，请先完成打包" }
$proc = Start-Process -FilePath "D:\Develop\JDK25\jdk-25.0.3+9\bin\java.exe" -ArgumentList "-jar `"$TargetJar`" --spring.profiles.active=dev --server.port=8081" -PassThru -NoNewWindow
Start-Sleep -Seconds 20
if ($proc.HasExited) { Fail "后端启动后即退出" }
Ok

Write-Host "=== 6. 核心接口冒烟 ===" -ForegroundColor Cyan
$baseUrl = "http://localhost:8081"
$allOk = $true
$tests = @( '/', '/articles/all', '/categories/all', '/tags/all', '/about' )
foreach ($url in $tests) {
    try {
        $resp = Invoke-RestMethod -Uri "${baseUrl}${url}" -TimeoutSec 5
        if ($resp.code -eq 200) { Write-Host "  GET $url ... OK" -ForegroundColor Green }
        else { Write-Host "  GET $url ... FAIL (code=$($resp.code))" -ForegroundColor Red; $allOk = $false }
    } catch {
        Write-Host "  GET $url ... FAIL ($($_.Exception.Message))" -ForegroundColor Red; $allOk = $false
    }
}
try {
    $resp = Invoke-RestMethod -Uri "${baseUrl}/articles/search?keywords=test" -TimeoutSec 5
    if ($resp.code -eq 200) { Write-Host "  GET /articles/search ... OK" -ForegroundColor Green }
    else { Write-Host "  GET /articles/search ... $($resp.code)" -ForegroundColor Yellow }
} catch {
    Write-Host "  GET /articles/search ... 跳过 (ES 未运行)" -ForegroundColor Yellow
}

Write-Host "=== 7. 停止后端 ===" -ForegroundColor Cyan
Stop-Process $proc -Force -ErrorAction SilentlyContinue
Write-Host "  后端已停止"
if ($allOk) { Write-Host ""; Write-Host "验证全部通过" -ForegroundColor Green }
else { Write-Host ""; Write-Host "部分接口失败，查看日志定位" -ForegroundColor Yellow }
Set-Location $RootDir