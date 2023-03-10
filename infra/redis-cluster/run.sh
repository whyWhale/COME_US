#!/usr/bin/env sh

echo "volume remove not dangling! clean .ㅛ."
# shellcheck disable=SC2046
docker volume rm $(docker volume ls  -q --filter dangling=true)

echo "docker already duplicate container  remove 'ㅅ' "
docker rm -f redis-node1 redis-node2 redis-node3 redis-cluster-entry

echo "docker up ready :)"
docker-compose up