#!/bin/bash
set -e

# Setup working directories relative to script path
cd "$(dirname "$0")"

# Auto-detect or fall back to system GRAALVM_HOME/JAVA_HOME environment variables
if [ -z "$GRAALVM_HOME" ]; then
  if [ -n "$JAVA_HOME" ]; then
    export GRAALVM_HOME="$JAVA_HOME"
  else
    # Fallback to macOS java_home resolution tool if available
    if command -v /usr/libexec/java_home &> /dev/null; then
      export GRAALVM_HOME=$(/usr/libexec/java_home -v 21)
    fi
  fi
fi

# Ensure Native-Image binary is on PATH if environment has it mapped
if [ -n "$GRAALVM_HOME" ]; then
  export JAVA_HOME="$GRAALVM_HOME"
  export PATH="$GRAALVM_HOME/bin:$PATH"
fi

echo "=== Building Standard Jar & GraalVM Native Binary ==="
../gradlew clean nativeCompile assemble

echo ""
echo "=== Running 10 Runs on JVM (Standard JDK Hotspot) ==="
JVM_TOTAL=0
for i in {1..10}
do
  JVM_START=$(python3 -c 'import time; print(int(time.time() * 1000))')
  java -jar build/libs/advanced-example.jar > /dev/null
  JVM_END=$(python3 -c 'import time; print(int(time.time() * 1000))')
  JVM_DIFF=$((JVM_END - JVM_START))
  JVM_TOTAL=$((JVM_TOTAL + JVM_DIFF))
  echo "  Run $i: ${JVM_DIFF} ms"
done
JVM_AVG=$((JVM_TOTAL / 10))
echo "JVM Average Cold Execution: ${JVM_AVG} ms"

echo ""
echo "=== Running 10 Runs as GraalVM Native Binary ==="
NATIVE_TOTAL=0
for i in {1..10}
do
  NATIVE_START=$(python3 -c 'import time; print(int(time.time() * 1000))')
  ./build/native/nativeCompile/valix-advanced-native > /dev/null
  NATIVE_END=$(python3 -c 'import time; print(int(time.time() * 1000))')
  NATIVE_DIFF=$((NATIVE_END - NATIVE_START))
  NATIVE_TOTAL=$((NATIVE_TOTAL + NATIVE_DIFF))
  echo "  Run $i: ${NATIVE_DIFF} ms"
done
NATIVE_AVG=$((NATIVE_TOTAL / 10))
echo "Native Binary Average Cold Execution: ${NATIVE_AVG} ms"

SPEEDUP=$(python3 -c "print(round(${JVM_AVG} / ${NATIVE_AVG}, 2))")
echo ""
echo "=== Cold Start Speedup Factor: ${SPEEDUP}x Faster ==="
echo "====================================================="
