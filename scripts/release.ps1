Param(
  [Parameter(Mandatory=$false, Position=0)] [string]$Version,
  [switch]$BumpPatch
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path | Split-Path -Parent
Set-Location $repoRoot

$currentVersion = if (Test-Path VERSION) { (Get-Content VERSION -Raw).Trim() } else { '1.0.0' }

if (-not $Version -and -not $BumpPatch) {
  Write-Host "Usage: ./scripts/release.ps1 <version> | -BumpPatch" -ForegroundColor Yellow
  exit 1
}

if ($BumpPatch) {
  $parts = $currentVersion.Split('.')
  if ($parts.Length -lt 3) { throw "Current version $currentVersion not semantic x.y.z" }
  $parts[2] = [int]$parts[2] + 1
  $Version = $parts -join '.'
}

if ($Version.StartsWith('0')) { throw "Native distribution requires MAJOR > 0 (got $Version)" }

Set-Content -Path VERSION -Value "$Version`n" -Encoding utf8

Write-Host "==> Building release version $Version" -ForegroundColor Cyan
./gradlew clean prepareRelease --no-daemon

Write-Host "==> Creating git tag v$Version" -ForegroundColor Cyan
 git add VERSION
 git diff --cached --quiet || git commit -m "chore(release): v$Version"
 git tag -a "v$Version" -m "Project KARL $Version"

Write-Host "==> Push with: git push origin main --tags" -ForegroundColor Green
