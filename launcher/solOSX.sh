#!/usr/bin/env bash
# TODO: Target the embedded JRE again when it'll listen to arguments or otherwise find the magic trick to allow that
#lwjreOSX/bin/java -XstartOnFirstThread -jar libs/solDesktop.jar -noSplash
java -XstartOnFirstThread -jar libs/solDesktop.jar -noSplash
