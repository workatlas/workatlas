@rem ==========================================================================
@rem Gradle startup script for Windows
@rem ==========================================================================
@echo off
setlocal

if not defined JAVA_HOME (
    if exist "C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\OpenJDK" (
        set "JAVA_HOME=C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\OpenJDK"
    )
)
if not defined ANDROID_HOME (
    if exist "C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\SDK" (
        set "ANDROID_HOME=C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\SDK"
    )
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "GRADLE_LAUNCHER=C:\Program Files\Unity\Hub\Editor\6000.6.7f1\Editor\Data\PlaybackEngines\AndroidPlayer\Tools\gradle\lib\gradle-launcher-9.3.1.jar"

"%JAVA_HOME%\bin\java.exe" -classpath "%GRADLE_LAUNCHER%" org.gradle.launcher.GradleMain %*
exit /b %ERRORLEVEL%
