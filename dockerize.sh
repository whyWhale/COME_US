#!/usr/bin/env sh
./gradlew clean :bootJar

docker build --force-rm -t ${IMG_NAME:-oder-renewal_order-renewal-application} .