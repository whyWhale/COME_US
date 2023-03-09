#!/bin/bash
echo "docker run"
docker rm -f localstack
echo "compose up"
docker-compose up