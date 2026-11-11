if (-not $env:JAVA_HOME) {
    if (Test-Path "C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\OpenJDK") {
        $env:JAVA_HOME = "C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\OpenJDK"
    }
}
if (-not $env:ANDROID_HOME) {
    if (Test-Path "C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\SDK") {
        $env:ANDROID_HOME = "C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\SDK"
    }
}
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

$launcher = "C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\Tools\gradle\lib\gradle-launcher-9.3.1.jar"

& "$env:JAVA_HOME\bin\java.exe" -classpath $launcher org.gradle.launcher.GradleMain $args
exit $LASTEXITCODE
