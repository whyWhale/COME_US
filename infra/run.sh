#!/usr/bin/env sh
echo "network create : order-renewal-part"
docker network create order-renewal-part

echo "volume remove not dangling! clean .ㅛ."
# shellcheck disable=SC2046
docker volume rm $(docker volume ls  -q --filter dangling=true)

echo "mysql redo log delete :)"
rm -r ./mysql-replication/master/data

echo "docker already duplicate container  remove 'ㅅ' "
docker rm -f mysql-master mysql-slave-1 mysql-phpmyadmin redis redis-commander redis-node1 redis-node2 redis-node3 redis-cluster-entry localstack nginx

echo "docker up ready :)"
docker-compose up