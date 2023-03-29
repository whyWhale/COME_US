#!/usr/bin/env sh

docker rm -f controller agent

# ngrinder 실행
docker run -d -v ~/ngrinder-controller:/opt/ngrinder-controller --name controller -p 8088:80 -p 16001:16001 -p 12000-12009:12000-12009 ngrinder/controller

sleep 10s

# agent 실행
docker run -d --name agent --link controller:controller ngrinder/agent

# refer: https://hub.docker.com/r/ngrinder/controller/