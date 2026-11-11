#!/usr/bin/env sh
if [ -z "$JAVA_HOME" ]; then
    export JAVA_HOME="/c/Program Files/Unity/Hub/Editor/6000.6.7f1/Editor/Data/PlaybackEngines/AndroidPlayer/OpenJDK"
fi
if [ -z "$ANDROID_HOME" ]; then
    export ANDROID_HOME="/c/Program Files/Unity/Hub/Editor/6000.6.7f1/Editor/Data/PlaybackEngines/AndroidPlayer/SDK"
fi
export PATH="$JAVA_HOME/bin:$PATH"
java -classpath "C:/Program Files/Unity/Hub/Editor/6000.6.7f1/Editor/Data/PlaybackEngines/AndroidPlayer/Tools/gradle/lib/gradle-launcher-9.3.1.jar" org.gradle.launcher.GradleMain "$@"
