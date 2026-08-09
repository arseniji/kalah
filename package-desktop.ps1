# Собирает переносимый образ приложения (папка с Kalah.exe и вшитой JRE) и пакует его в zip.
# Установщик не создаётся: --type app-image не требует ни WiX, ни прав администратора.
#
#   powershell -ExecutionPolicy Bypass -File package-desktop.ps1

$ErrorActionPreference = "Stop"

$root       = $PSScriptRoot
$appVersion = "0.1.0"                       # jpackage не принимает суффикс -SNAPSHOT
$mainJar    = "kalah-desktop-$appVersion-SNAPSHOT.jar"
$mainClass  = "io.github.arseniji.kalah.desktop.DesktopApp"

$appDir  = Join-Path $root "kalah-desktop\target\app"
$distDir = Join-Path $root "kalah-desktop\target\dist"
$zipPath = Join-Path $root "kalah-desktop\target\Kalah-$appVersion-win.zip"

# 1. Собрать модули. Нужен install, иначе kalah-desktop не найдёт соседей.
Write-Host "== сборка ==" -ForegroundColor Cyan
& "$root\mvnw.cmd" -B install -DskipTests
if ($LASTEXITCODE -ne 0) { throw "сборка упала" }

# 2. Положить собственный jar рядом с зависимостями: jpackage требует,
#    чтобы главный jar лежал внутри --input.
Copy-Item (Join-Path $root "kalah-desktop\target\$mainJar") $appDir -Force

# 3. Собрать образ приложения.
Write-Host "== jpackage ==" -ForegroundColor Cyan
if (Test-Path $distDir) { Remove-Item $distDir -Recurse -Force }
& jpackage `
    --type app-image `
    --name Kalah `
    --app-version $appVersion `
    --input $appDir `
    --main-jar $mainJar `
    --main-class $mainClass `
    --dest $distDir `
    --java-options "--enable-native-access=ALL-UNNAMED"
if ($LASTEXITCODE -ne 0) { throw "jpackage упал" }

# 4. Упаковать в zip.
if (Test-Path $zipPath) { Remove-Item $zipPath -Force }
Compress-Archive -Path (Join-Path $distDir "Kalah") -DestinationPath $zipPath

$mb = [math]::Round((Get-Item $zipPath).Length / 1MB, 1)
Write-Host "`nготово: $zipPath ($mb МБ)" -ForegroundColor Green
Write-Host "запуск: $distDir\Kalah\Kalah.exe"
