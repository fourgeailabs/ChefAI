#!/bin/sh

# Attempt to use globally installed gradle if available, otherwise bootstrap or run java wrapper
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi

# Directory where gradlew lives
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
    exec "$JAVACMD" -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
else
    # Fallback to installing or running gradle directly
    echo "Warning: gradle-wrapper.jar not found, attempting gradle invocation..."
    if command -v gradle >/dev/null 2>&1; then
        exec gradle "$@"
    else
        echo "Error: Gradle is not installed and wrapper jar is not present."
        exit 1
    fi
fi
