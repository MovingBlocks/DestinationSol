#!/usr/bin/env bash

JRE=lwjreOSX/bin/java
ARCH=$(uname -m)
if [[ "$ARCH" == "x86_64" ]]; then
  JRE=lwjreOSX/bin/java
elif [[ "$ARCH" == "arm64" ]]; then
  JRE=lwjreOSArm/bin/java
else
  echo "Unsupported architecture $ARCH"
  exit 1
fi

$JRE -XstartOnFirstThread -jar libs/solDesktop.jar -noSplash