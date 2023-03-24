#!/usr/bin/env sh
./gradlew clean :bootJar

docker build --force-rm -t ${APPLICATION_IMAGE} .