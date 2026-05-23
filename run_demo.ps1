# =============================================================================
#  HealthCare Appointment Management System — Demo Runner
#  Run with:  .\run_demo.ps1
# =============================================================================

$ROOT = $PSScriptRoot

# ── 1. Locate javac ───────────────────────────────────────────────────────────
$javac = Get-Command javac -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source
if (-not $javac) {
    $candidates = @(
        "C:\Program Files (x86)\Android\openjdk\jdk-17.0.8.101-hotspot\bin\javac.exe",
        "C:\Program Files\Android\jdk\jdk-8.0.302.8-hotspot\jdk8u302-b08\bin\javac.exe"
    )
    foreach ($c in $candidates) {
        if (Test-Path $c) { $javac = $c; break }
    }
}
if (-not $javac) {
    # Last resort: scan common locations
    $javac = Get-ChildItem "C:\Program Files", "C:\Program Files (x86)" `
        -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue -Depth 7 |
        Sort-Object { $_.FullName } -Descending |
        Select-Object -ExpandProperty FullName -First 1
}
if (-not $javac) {
    Write-Error "javac not found. Please install a JDK and add it to PATH."
    exit 1
}

$java = Join-Path (Split-Path $javac) "java.exe"
Write-Host "Using JDK: $(Split-Path (Split-Path $javac) -Leaf)" -ForegroundColor DarkGray

# ── 2. Compile ────────────────────────────────────────────────────────────────
$out   = "$ROOT\out"
$src   = "$ROOT\src"
$files = Get-ChildItem -Recurse -Filter "*.java" $src | Select-Object -ExpandProperty FullName

New-Item -ItemType Directory -Force -Path $out | Out-Null

Write-Host "`nCompiling $($files.Count) source files..." -ForegroundColor Cyan
$errors = & $javac -encoding UTF-8 -d $out $files 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed:`n$errors"
    exit 1
}
Write-Host "Compilation successful." -ForegroundColor Green

# ── 3. Run demo ───────────────────────────────────────────────────────────────
Write-Host ""
& $java -cp $out com.healthcare.appointment.demo.HealthcareSystemDemo

# ── 4. Pause (useful when launched outside VS Code / double-click) ────────────
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor DarkGray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
